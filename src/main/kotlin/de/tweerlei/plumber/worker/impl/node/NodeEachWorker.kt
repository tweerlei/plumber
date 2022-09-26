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
import de.tweerlei.plumber.worker.types.Node
import de.tweerlei.plumber.worker.types.StringValue
import de.tweerlei.plumber.worker.types.Value
import mu.KLogging

class NodeEachWorker(
    private val ptr: JsonPointer,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging() {
        private val INDEX_ZERO = StringValue.of("0")
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        item.getAs<Node>(WellKnownKeys.NODE)
            .value.at(ptr).toMap().all { (key, value) ->
                fn(WorkItem.of(value,
                    WellKnownKeys.NAME to key
                ))
            }
    }

    private fun JsonNode.toMap(): Map<StringValue, Value> =
        when {
            isArray -> withIndex().associate { v -> StringValue.of(v.index.toString()) to v.value.toComparableValue() }
            isObject -> fields().asSequence().associate { v -> StringValue.of(v.key) to v.value.toComparableValue() }
            isEmpty -> emptyMap()
            else -> mapOf(INDEX_ZERO to toComparableValue())
        }
}
