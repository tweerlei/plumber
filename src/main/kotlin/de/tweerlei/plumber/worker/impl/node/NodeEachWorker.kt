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
import mu.KLogging

class NodeEachWorker(
    private val ptr: JsonPointer,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        item.getAs<JsonNode>(WellKnownKeys.NODE)
            .at(ptr).toMap().all { (key, value) ->
                fn(WorkItem.of(value,
                    WellKnownKeys.NAME to key
                ))
            }
    }

    private fun JsonNode.toMap(): Map<String, Any?> =
        when {
            isBoolean -> mapOf("0" to booleanValue())
            isNumber -> mapOf("0" to numberValue())
            isTextual -> mapOf("0" to textValue())
            isBinary -> mapOf("0" to binaryValue())
            isArray -> withIndex().associate { v -> v.index.toString() to v.value }
            isObject -> fields().asSequence().associate { v -> v.key to v.value }
            isNull -> mapOf("0" to null)
            isEmpty -> emptyMap()
            else -> mapOf("0" to this)
        }
}
