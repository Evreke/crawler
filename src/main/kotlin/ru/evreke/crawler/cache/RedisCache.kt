package ru.evreke.crawler.cache

import io.lettuce.core.*
import io.lettuce.core.api.coroutines

class RedisCache {
    private val connection by lazy { RedisClient.create("redis://localhost:6379").connect().coroutines() }

    fun getKeys(pattern: String) = connection.keys(pattern)

    suspend fun set(key: String, value: String): String? = connection.set(key, value)
    suspend fun append(key: String, value: String): Long? = connection.append(key, value)
    suspend fun setList(key: String, vararg values: String): Long? = connection.rpush(key, *values)
    suspend fun getList(key: String, startIdx: Long = 0, endIdx: Long = -1): List<String> = connection.lrange(key, startIdx, endIdx)
    suspend fun lPop(key: String): String? = connection.lpop(key)
    suspend fun lPop(key: String, count: Long): List<String> = connection.lpop(key, count)
    suspend fun lLen(key: String): Long? = connection.llen(key)
    suspend fun exists(key: String): Boolean = connection.exists(key) == 1L
    suspend fun notExists(key: String): Boolean = !exists(key)

    suspend fun sAdd(key: String, vararg values: String): Long? = connection.sadd(key, *values)
    suspend fun sPop(key: String): String? = connection.spop(key)
    suspend fun sPop(key: String, count: Long): Set<String> = connection.spop(key, count)
    suspend fun sScan(key: String, scanCursor: ScanCursor, scanArgs: ScanArgs): ValueScanCursor<String>? =
        connection.sscan(key, scanCursor, scanArgs)
}