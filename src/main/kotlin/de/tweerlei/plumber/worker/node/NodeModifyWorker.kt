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
package de.tweerlei.plumber.worker.node

import com.fasterxml.jackson.core.JsonPointer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.DelegatingWorker
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.Worker

class NodeModifyWorker(
    private val p: JsonPointer,
    private val objectMapper: ObjectMapper,
    worker: Worker
): DelegatingWorker(worker) {

    private val ptr = p.head()
    private val key = p.last().matchingProperty
    private val index = p.last().matchingIndex

    override fun doProcess(item: WorkItem) =
        item.getOrSetAs<JsonNode>(WellKnownKeys.NODE) {
            JsonNodeFactory.instance.objectNode()
        }
            .at(ptr)
            .let { node ->
                when {
                    node.isObject -> (node as ObjectNode).set<JsonNode>(key, objectMapper.valueToTree(item.getOptional()))
                        .let { true }
                    node.isArray -> (node as ArrayNode).set(index, objectMapper.valueToTree(item.getOptional()))
                        .let { true }
                    else -> false
                }
            }
}
