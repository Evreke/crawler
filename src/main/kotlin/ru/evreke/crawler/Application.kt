package ru.evreke.crawler

import io.ktor.client.request.*
import io.ktor.http.*
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.nio.gml.GmlExporter
import ru.evreke.crawler.cache.RedisCache
import ru.evreke.crawler.core.client
import ru.evreke.crawler.core.parsePageUrls
import ru.evreke.crawler.core.queue.RedisSetQueue
import java.io.File


val redisCache by lazy { RedisCache() }
val queue by lazy { RedisSetQueue(redisCache) }
val excludedUrl by lazy { mutableListOf("twitter.com", "facebook.com", "instagram.com") }
val queueKey = "${RedisKeys.QUEUE}:${RedisKeys.URL}"
fun Url.toHostWithEncodedPath() = "$host$encodedPath"
fun List<Url>.toUrls() = map { it.toHostWithEncodedPath() }


suspend fun main() {
//    val url = System.getenv("INIT_PARSING_URL")?.let { Url(it) }
//        ?: readLine()?.let { Url(it) }
//        ?: throw RuntimeException("No url provided")
//
//    runBlocking {
//        withContext(Dispatchers.Default) {
//            parseSingle(url)
//        }
//        launch { parseInfinitely() }
//    }

    data class Vertex(val url: String)
    
    val g = DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    

    redisCache.getKeys("PARSED:*").collect { key ->
        getSetValues(key).forEach {
            val vertex = it.key.split("PARSED:")[1]
            g.addVertex(vertex)
            it.value.forEach { value ->
                g.addVertex(value)
                g.addEdge(vertex, value)
            }
        }
    }

    val exporter = GmlExporter<String, DefaultEdge>()
    exporter.exportGraph(g, File("hello.gml"))
//    val writer: Writer = StringWriter()
//    exporter.exportGraph(hrefGraph, writer)
//    System.out.println(writer.toString())
}

suspend fun getSetValues(key: String): Map<String, MutableList<String>> {
    tailrec suspend fun scan(key: String, cursor: ScanCursor, scanArgs: ScanArgs, list: MutableList<String>): MutableList<String> {
        val result = redisCache.sScan(key, cursor, scanArgs)
        result?.let { list.addAll(it.values) }
        return when {
            result != null && result.isFinished -> list
            result == null -> list
            else -> scan(key, ScanCursor(result.cursor, result.isFinished), scanArgs, list)
        }
    }
    scan(key, ScanCursor.INITIAL, ScanArgs().limit(10), mutableListOf()).let {
        return mapOf(key to it)
    }
}

suspend fun parseSingle(url: Url) {
    val parsed = "${RedisKeys.PARSED}:$url"
    if (redisCache.notExists(parsed)) {
        parseUrls(url).also { urls ->
            if (urls.isNotEmpty()) {
                urls.map { it.toString() }.toTypedArray()
                    .also { redisCache.sAdd(parsed, *it) }
                    .filter { redisCache.notExists("${RedisKeys.PARSED}:$it") }
                    .takeIf { it.isNotEmpty() }
                    ?.also { queue.add(*it.toTypedArray()) }
            }
        }
    } else {
        println("Page $parsed already parsed")
    }
}

suspend fun parseInfinitely() {
    coroutineScope {
        while (true) {
            val url = queue.popFew(queueKey, 5L).filter { redisCache.notExists(it) }
            if (url.isNotEmpty()) {
                url.forEach {
                    launch {
                        println("Parsing page -> $it")
                        parseSingle(Url(it))
                    }
                }
                delay(500)
            } else {
                println("Очередь пустая")
                delay(2000)
            }
        }
    }
}

enum class RedisKeys(val key: String) {
    PARSED("parsed"),
    QUEUE("queue"),
    URL("url"),
}

private suspend fun parseUrls(url: Url): List<Url> = getPage(url).let { result ->
    if (result.isSuccess) {
        result.toString()
            .parsePageUrls()
            .filter { it.isNotBlank() }
            .map { Url(it) }
            .filter { it.host == url.host }
    } else {
        emptyList()
    }
}

suspend fun getPage(url: Url) = try {
    println("Receiving page: $url")
    Result.success<String>(client.get(url))
} catch (e: Exception) {
    Result.failure<Exception>(e)
}
