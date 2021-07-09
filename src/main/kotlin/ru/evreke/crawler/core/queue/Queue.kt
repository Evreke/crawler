package ru.evreke.crawler.core.queue

interface Queue<V, T> {
    suspend fun add(vararg value: T)
    suspend fun pop(value: V): T?
    suspend fun isEmpty(value: V): Boolean
    fun size(value: V): Long
}

interface SetQueue<V, T> : Queue<V, T> {
    suspend fun popFew(value: V, count: Long): Set<T>
}