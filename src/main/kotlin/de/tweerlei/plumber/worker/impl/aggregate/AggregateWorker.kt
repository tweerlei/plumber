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
package de.tweerlei.plumber.worker.impl.aggregate

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import java.util.concurrent.ConcurrentHashMap

abstract class AggregateWorker<V>(
    worker: Worker
): DelegatingWorker(worker) {

    private val aggregates = ConcurrentHashMap<String, V>()

    override final fun doProcess(item: WorkItem) =
        (item.getOptional(WellKnownKeys.GROUP)?.toString() ?: "")
            .let { key ->
                update(key, item)
                    .let { updatedCounter ->
                        shouldPassOn(item, key, updatedCounter)
                    }
            }

    private fun update(key: String, item: WorkItem) =
        aggregates.computeIfAbsent(key, ::createAggregate)
            .let { currentValue ->
                aggregates.compute(key) { key, counter ->
                    updateGroupState(item, key, counter ?: currentValue)
                } ?: currentValue
            }

    abstract fun updateGroupState(item: WorkItem, key: String, aggregate: V): V

    abstract fun createAggregate(key: String): V

    abstract fun shouldPassOn(item: WorkItem, key: String, aggregate: V): Boolean

    override final fun onClose() {
        aggregates.forEach { (k, v) ->
            groupStateOnClose(k, v)
        }
    }

    abstract fun groupStateOnClose(key: String, aggregate: V)
}
