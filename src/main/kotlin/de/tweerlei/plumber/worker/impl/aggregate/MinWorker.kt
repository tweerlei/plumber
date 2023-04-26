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
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.ComparableValue
import de.tweerlei.plumber.worker.types.toComparableValue
import mu.KLogging

class MinWorker(
    private val name: String,
    private val interval: Long,
    worker: Worker
): AggregateWorker<Pair<Long, ComparableValue?>>(worker) {

    companion object: KLogging()

    override fun createAggregate(key: String) =
        Pair(0L, null)

    override fun updateGroupState(item: WorkItem, key: String, aggregate: Pair<Long, ComparableValue?>) =
        item.get()
            .toComparableValue()
            .let { value ->
                Pair(aggregate.first + 1L, minOf(aggregate.second ?: value, value))
            }.also { incremented ->
                if (incremented.first % interval == 0L) {
                    logger.info { "$name: Items processed [$key]: ${incremented.first}, Min. item: ${incremented.second}" }
                }
            }

    override fun shouldPassOn(item: WorkItem, key: String, aggregate: Pair<Long, ComparableValue?>): Boolean {
        aggregate.second?.let { minimum ->
            item.set(minimum, WellKnownKeys.MIN)
        }
        return true
    }

    override fun groupStateOnClose(key: String, aggregate: Pair<Long, ComparableValue?>) {
        logger.info { "$name: Min. item [$key]: ${aggregate.second}" }
    }
}
