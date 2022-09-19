package de.tweerlei.plumber.worker.types

import mu.KLogging
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class ValueCache<T, U>(
    private val name: String,
    private val filter: (T) -> Boolean = { true },
    private val creator: (T) -> U
) {

    companion object: KLogging() {
        private val allCaches = mutableMapOf<String, ValueCache<*, *>>()
        var useCache = false

        fun dumpAll() =
            allCaches.forEach { (_, cache) ->
                logger.debug { cache.dump() }
            }
    }

    init {
        allCaches[name] = this
    }

    private val cache = WeakHashMap<T, U>(100)
    private val gets = AtomicLong()
    private val hits = AtomicLong()

    fun getOrCreateValue(value: T) =
        when (useCache && filter(value)) {
            true -> cache[value].also {
                gets.incrementAndGet()
            }.let { cached ->
                when (cached) {
                    null -> creator(value).also { cache[value] = it }
                    else -> cached.also { hits.incrementAndGet() }
                }
            }
            false -> creator(value)
        }

    fun dump() =
        "Cache stats for $name: ${hits.get()}/${gets.get()} cache hits, current size: ${cache.size}"
}
