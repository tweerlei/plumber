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

class WorkItemList private constructor(
    private val items: MutableList<WorkItem>
): Value {

    companion object {
        const val NAME = "items"

        fun ofSize(initialCapacity: Int) =
            WorkItemList(ArrayList(initialCapacity))
    }

    override fun getName() =
        NAME

    override fun toAny() =
        items
    override fun toBoolean() =
        items.isNotEmpty()
    override fun toLong() =
        items.size.toLong()
    override fun toDouble() =
        items.size.toDouble()
    override fun toByteArray() =
        byteArrayOf()
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.arrayNode().also { node ->
            items.forEach { value -> node.add(value.get().toJsonNode()) }
        }

    override fun toRange() =
        Range()
    override fun toRecord() =
        Record.of(this)

    override fun size() =
        items.size.toLong()
    override fun equals(other: Any?) =
        other is WorkItemList &&
                items == other.items
    override fun hashCode() =
        items.hashCode()
    override fun toString() =
        items.toString()
    override fun dump() =
        items.map { value -> value.dump() }.toString()
}
