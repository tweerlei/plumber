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
import de.tweerlei.plumber.worker.types.DoubleValue
import mu.KLogging

class AveragingWorker(
    private val name: String,
    private val interval: Long,
    worker: Worker
): AggregateWorker<Pair<Long, Long>>(worker) {

    companion object: KLogging()

    override fun createAggregate(key: String) =
        Pair(0L, 0L)

    override fun updateGroupState(item: WorkItem, key: String, aggregate: Pair<Long, Long>) =
        (item.getOptional(WellKnownKeys.SIZE)?.toLong() ?: item.get().toLong())
            .let { size ->
                Pair(aggregate.first + size, aggregate.second + 1L)
                    .also { incremented ->
                        if (incremented.second % interval == 0L) {
                            logger.info { "$name: Items processed [$key]: ${incremented.second}, Average: ${incremented.first.toDouble() / incremented.second.toDouble()}" }
                        }
                    }
            }

    override fun shouldPassOn(item: WorkItem, key: String, aggregate: Pair<Long, Long>): Boolean {
        item.set(DoubleValue.of(aggregate.first.toDouble() / aggregate.second.toDouble()), WellKnownKeys.AVG)
        return true
    }

    override fun groupStateOnClose(key: String, aggregate: Pair<Long, Long>) {
        logger.info { "$name: Average [$key]: ${aggregate.first.toDouble() / aggregate.second.toDouble()}" }
    }
}
