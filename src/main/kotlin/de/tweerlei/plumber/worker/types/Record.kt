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
package de.tweerlei.plumber.worker.types

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory

class Record(
    private val map: MutableMap<String, Value> = LinkedHashMap()
): Value {

    companion object {
        const val NAME = "record"

        fun of(vararg items: Pair<String, Value>) =
            Record().apply {
                items.forEach { (k, v) -> map[k] = v }
            }
        fun of(vararg items: Value) =
            Record().apply {
                items.forEachIndexed { index, value ->
                    map[index.toString()] = value
                }
            }
        fun ofComparableValues(items: Array<String>) =
            Record().apply {
                items.forEachIndexed { index, value ->
                    map[index.toString()] = value.toComparableValue()
                }
            }
        fun ofCollection(items: Collection<*>) =
            Record().also { record ->
                items.forEachIndexed { index, value -> record.map[index.toString()] = value.toValue() }
            }
        fun ofMap(items: Map<*, *>) =
            Record().also { record ->
                items.forEach { (key, value) -> record.map[key.toString()] = value.toValue() }
            }
    }

    override fun getName() =
        NAME

    override fun toAny() =
        map
    override fun toBoolean() =
        map.isNotEmpty()
    override fun toLong() =
        map.size.toLong()
    override fun toDouble() =
        map.size.toDouble()
    override fun toByteArray() =
        map.toString().toByteArray()
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.objectNode().also { node ->
            map.forEach { (key, value) -> node.set<JsonNode>(key, value.toJsonNode()) }
        }

    override fun toRange() =
        Range(
            map["0"].toComparableValue(),
            map["1"].toComparableValue()
        )
    override fun toRecord() =
        this

    fun getValue(key: String) =
        map.getOrDefault(key, NullValue.INSTANCE)

    override fun size() =
        map.size.toLong()
    override fun equals(other: Any?) =
        other is Record &&
                map == other.map
    override fun hashCode() =
        map.hashCode()
    override fun toString() =
        map.toString()
    override fun dump() =
        map.mapValues { (_, value) -> value.dump() }.toString()
}
