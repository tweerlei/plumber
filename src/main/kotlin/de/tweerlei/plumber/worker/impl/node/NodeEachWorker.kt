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
package de.tweerlei.plumber.worker.impl.node

import com.fasterxml.jackson.core.JsonPointer
import com.fasterxml.jackson.databind.JsonNode
import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys

class NodeEachWorker(
    private val ptr: JsonPointer,
    limit: Int,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        item.getAs<JsonNode>(WellKnownKeys.NODE)
            .also { json ->
                json.at(ptr).toIterable().all { fn(WorkItem.of(it)) }
            }
    }

    private fun JsonNode.toIterable(): Iterable<Any?> =
        when {
            isBoolean -> listOf(booleanValue())
            isNumber -> listOf(numberValue())
            isTextual -> listOf(textValue())
            isBinary -> listOf(binaryValue())
            isArray -> this
            isNull -> listOf(null)
            isEmpty -> emptyList()
            else -> listOf(this)
        }
}
