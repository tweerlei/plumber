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

class Record: LinkedHashMap<String, Value>(), Value {

    companion object {
        fun of(vararg items: Pair<String, Value>) =
            Record().apply {
                items.forEach { (k, v) -> this[k] = v }
            }

        fun from(items: Array<String>) =
            Record().apply {
                items.forEachIndexed { index, value ->
                    this[index.toString()] = value.toComparableValue()
                }
            }
    }

    override fun getName() =
        "record"

    override fun toAny() =
        this
    override fun toBoolean() =
        isNotEmpty()
    override fun toNumber() =
        size.toLong()
    override fun toByteArray() =
        toString().toByteArray()
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.objectNode().also { node ->
            forEach { key, value -> node.set<JsonNode>(key, value.toJsonNode()) }
        }
    override fun size() =
        size.toLong()

    override fun dump() =
        mapValues { (_, value) -> value.dump() }.toString()
}
