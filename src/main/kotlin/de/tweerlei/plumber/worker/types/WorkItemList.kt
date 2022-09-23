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
import de.tweerlei.plumber.worker.WorkItem

class WorkItemList(initialCapacity: Int): ArrayList<WorkItem>(initialCapacity), Value {

    companion object {
        const val NAME = "items"
    }

    override fun getName() =
        NAME

    override fun toAny() =
        this
    override fun toBoolean() =
        isNotEmpty()
    override fun toLong() =
        size.toLong()
    override fun toDouble() =
        size.toDouble()
    override fun toByteArray() =
        byteArrayOf()
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.arrayNode().also { node ->
            forEach { value -> node.add(value.get().toJsonNode()) }
        }

    override fun dump() =
        map { value -> value.dump() }.toString()
    override fun size() =
        size.toLong()
}
