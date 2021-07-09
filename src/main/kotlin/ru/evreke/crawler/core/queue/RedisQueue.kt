package ru.evreke.crawler.core.queue

import ru.evreke.crawler.RedisKeys
import ru.evreke.crawler.cache.RedisCache

class RedisQueue(
    private val redisClient: RedisCache
) : Queue<String, String> {

    companion object {
        private val QUEUE_KEY = "${RedisKeys.QUEUE}:${RedisKeys.URL}"
    }

    override suspend fun add(vararg value: String) {
        redisClient.setList(QUEUE_KEY, *value)
    }

    override suspend fun pop(value: String): String? = redisClient.lPop(value)

    override suspend fun isEmpty(value: String): Boolean = redisClient.lLen(value) != 0L
    override fun size(value: String): Long {
        TODO("Not yet implemented")
    }

}

class RedisSetQueue(
    private val redisClient: RedisCache
) : SetQueue<String, String> {

    companion object {
        private val QUEUE_KEY = "${RedisKeys.QUEUE}:${RedisKeys.URL}"
    }

    override suspend fun add(vararg value: String) {
        redisClient.sAdd(QUEUE_KEY, *value)
    }

    override suspend fun pop(value: String): String? = redisClient.sPop(value)
    override suspend fun popFew(value: String, count: Long): Set<String> = redisClient.sPop(value, count)
    override suspend fun isEmpty(value: String): Boolean = redisClient.lLen(value) != 0L

    override fun size(value: String): Long {
        TODO("Not yet implemented")
    }

}