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

import de.tweerlei.plumber.util.Stopwatch
import de.tweerlei.plumber.util.humanReadable
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.LongValue
import mu.KLogging

class SummingWorker(
    private val name: String,
    private val interval: Long,
    worker: Worker
): AggregateWorker<Pair<Long, Stopwatch>>(worker) {

    companion object: KLogging()

    private lateinit var stopwatch: Stopwatch

    override fun onOpen() {
        stopwatch = Stopwatch()
    }

    override fun createAggregate(key: String) =
        Pair(0L, stopwatch)

    override fun updateGroupState(item: WorkItem, key: String, aggregate: Pair<Long, Stopwatch>) =
        (item.getOptional(WellKnownKeys.SIZE)?.toLong() ?: item.get().toLong())
            .let { size ->
                (aggregate.first + size).let { incremented ->
                    if (incremented / interval > aggregate.first / interval) {
                        val bytes = interval * (incremented / interval - aggregate.first / interval)
                        val perSecond = aggregate.second.itemsPerSecond(bytes.toDouble())
                        logger.info { "$name: Item sum [$key]: $incremented @ ${perSecond.humanReadable()} byte/s" }
                        Pair(incremented, Stopwatch())
                    } else {
                        Pair(incremented, aggregate.second)
                    }
                }
            }

    override fun shouldPassOn(item: WorkItem, key: String, aggregate: Pair<Long, Stopwatch>): Boolean {
        item.set(LongValue.of(aggregate.first), WellKnownKeys.SUM)
        return true
    }

    override fun groupStateOnClose(key: String, aggregate: Pair<Long, Stopwatch>) {
        val perSecond = stopwatch.itemsPerSecond(aggregate.first.toDouble())
        logger.info { "$name: Item sum [$key]: ${aggregate.first}" }
        logger.info { "$name: Throughput [$key]: ${perSecond.humanReadable()} byte/s" }
    }
}
