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

import de.tweerlei.plumber.worker.types.NullValue
import de.tweerlei.plumber.worker.types.Value

class WorkItem private constructor(
    private val map: MutableMap<String, Value>
) {

    companion object {
        const val DEFAULT_KEY = ""

        fun of(value: Value = NullValue.INSTANCE, vararg entries: Pair<String, Value>) =
            WorkItem(HashMap()).also { item ->
                entries.forEach { (k, v) -> item.set(v, k) }
                item.set(value)
            }
    }

    fun mergeMissingFrom(item: WorkItem) =
        item.map.forEach { (k, v) -> map.putIfAbsent(k, v) }

    fun get(key: String = DEFAULT_KEY) =
        map[key] ?: NullValue.INSTANCE

    inline fun <reified T: Value> getAs(key: String = DEFAULT_KEY): T =
        get(key) as T

    fun getOptional(key: String = DEFAULT_KEY) =
        map[key]

    inline fun <reified T: Value> getOptionalAs(key: String = DEFAULT_KEY): T? =
        getOptional(key) as T?

    fun getFirst(vararg keys: String) =
        keys.toList().plus(DEFAULT_KEY)
            .first { key -> map.containsKey(key) }
            .let { key -> map.getValue(key) }

    inline fun <reified T: Value> getFirstAs(vararg keys: String): T =
        getFirst(*keys) as T

    fun getOrSet(key: String, fn: () -> Value) =
        if (map.containsKey(key))
            map.getValue(key)
        else
            fn().also { set(it, key) }

    inline fun <reified T: Value> getOrSetAs(key: String, noinline fn: () -> T): T =
        getOrSet(key, fn) as T

    fun set(value: Value, key: String = DEFAULT_KEY) {
        if (value is NullValue)
            map.remove(key)
        else
            map[key] = value
    }

    fun dump() =
        map.mapValues { (_, value) -> value.dump() }.toString()
}
