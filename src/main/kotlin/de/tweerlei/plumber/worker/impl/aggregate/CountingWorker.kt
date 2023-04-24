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
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class CountingWorker(
    private val name: String,
    private val interval: Long,
    worker: Worker
): AggregateWorker<AtomicLong>(worker) {

    companion object: KLogging()

    private lateinit var stopwatch: Stopwatch
    private val current = AtomicReference<Stopwatch>()

    override fun onOpen() {
        stopwatch = Stopwatch()
        current.set(stopwatch)
    }

    override fun createAggregate(key: String) =
        AtomicLong()

    override fun updateGroupState(item: WorkItem, key: String, aggregate: AtomicLong) =
        aggregate.incrementAndGet()
            .also { counter ->
                if (counter % interval == 0L) {
                    val last = current.getAndSet(Stopwatch())
                    val perSecond = last.itemsPerSecond(interval.toDouble())
                    logger.info { "$name[$key]: Items processed: $counter @ ${perSecond.humanReadable()} items/s" }
                }
                item.set(LongValue.of(counter), WellKnownKeys.COUNT)
            }.let { true }

    override fun groupStateOnClose(key: String, aggregate: AtomicLong) {
        val perSecond = stopwatch.itemsPerSecond(aggregate.get().toDouble())
        logger.info { "$name[$key]: Items processed: ${aggregate.get()}" }
        logger.info { "$name[$key]: Throughput: ${perSecond.humanReadable()} items/s" }
    }
}
