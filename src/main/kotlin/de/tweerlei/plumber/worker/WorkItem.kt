/*
 * Copyright 2022 tweerlei Wruck + Buchmeier GbR - http://www.tweerlei.de/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tweerlei.plumber.worker

class WorkItem private constructor(
    private val map: MutableMap<String, Any>
) {

    companion object {
        const val DEFAULT_KEY = ""

        fun of(value: Any?, vararg entries: Pair<String, Any?>) =
            WorkItem(HashMap()).also { item ->
                entries.forEach { (k, v) -> item.set(v, k) }
                item.set(value)
            }
    }

    fun plus(item: WorkItem) =
        HashMap(this.map)
            .apply { putAll(item.map) }
            .let { map -> WorkItem(map) }

    fun has(key: String = DEFAULT_KEY) =
        map.containsKey(key)

    fun get(key: String = DEFAULT_KEY) =
        map.getValue(key)

    inline fun <reified T: Any> getAs(key: String = DEFAULT_KEY): T =
        get(key) as T

    fun getOptional(key: String = DEFAULT_KEY) =
        map[key]

    inline fun <reified T: Any> getOptionalAs(key: String = DEFAULT_KEY): T? =
        getOptional(key) as T?

    fun getFirst(vararg keys: String) =
        keys.toList().plus(DEFAULT_KEY)
            .first { key -> map.containsKey(key) }
            .let { key -> map.getValue(key) }

    inline fun <reified T: Any> getFirstAs(vararg keys: String): T =
        getFirst(*keys) as T

    fun getOrSet(key: String, fn: () -> Any) =
        if (map.containsKey(key))
            map.getValue(key)
        else
            fn().also { map[key] = it }

    inline fun <reified T: Any> getOrSetAs(key: String, noinline fn: () -> T): T =
        getOrSet(key, fn) as T

    fun set(value: Any?, key: String = DEFAULT_KEY) {
        if (value == null)
            map.remove(key)
        else
            map[key] = value
    }

    override fun toString() =
        map.toString()
}
