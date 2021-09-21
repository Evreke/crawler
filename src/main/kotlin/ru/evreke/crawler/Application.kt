package ru.evreke.crawler

import io.ktor.client.request.*
import io.ktor.http.*
import ru.evreke.crawler.core.client
import ru.evreke.crawler.core.parsePageUrls
import ru.evreke.crawler.model.Site
import ru.evreke.crawler.persistence.Mongo


val excludedUrl by lazy { mutableListOf("twitter.com", "facebook.com", "instagram.com") }
fun Url.toHostWithEncodedPath() = "$host$encodedPath"
fun List<Url>.toUrls() = map { it.toHostWithEncodedPath() }


suspend fun main() {
    val url = Url("https://www.geeksforgeeks.org/")
    val links = extractPageLinksBy(url).toSet()

    Mongo.client.getDatabase("test").getCollection<Site>().also {
        it.insertOne(Site(url.toHostWithEncodedPath(), links.map { url -> url.toHostWithEncodedPath() }))
    }

}

suspend fun extractPageLinksBy(url: Url) = parseLinksBy(url).also { urls ->
    if (urls.isNotEmpty()) {
        urls.map { it.toString() }.toTypedArray()
    }
}

private suspend fun parseLinksBy(url: Url): List<Url> = getPage(url)
    .let { result ->
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
