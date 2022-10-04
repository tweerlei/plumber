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
package de.tweerlei.plumber.worker.impl.stats

import de.tweerlei.plumber.util.Stopwatch
import de.tweerlei.plumber.util.humanReadable
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.LongValue
import mu.KLogging
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class SummingWorker(
    private val name: String,
    private val interval: Long,
    worker: Worker
): DelegatingWorker(worker) {

    companion object: KLogging()

    private val sum = AtomicLong()
    private lateinit var stopwatch: Stopwatch
    private val current = AtomicReference<Stopwatch>()

    override fun onOpen() {
        stopwatch = Stopwatch()
        current.set(stopwatch)
    }

    override fun doProcess(item: WorkItem) =
        item.get(WellKnownKeys.SIZE).toLong()
            .let { size ->
                sum.addAndGet(size)
                    .also { counter ->
                        if (counter / interval > (counter - size) / interval) {
                            val last = current.getAndSet(Stopwatch())
                            val bytes = interval * (counter / interval - (counter - size) / interval)
                            val perSecond = last.perSecond(bytes.toDouble())
                            logger.info { "$name: Item sum: $counter @ ${perSecond.humanReadable()} byte/s" }
                        }
                        item.set(LongValue.of(counter), WellKnownKeys.SUM)
                    }
            }.let { true }

    override fun onClose() {
        val perSecond = stopwatch.perSecond(sum.get().toDouble())
        logger.info { "$name: Item sum: ${sum.get()}" }
        logger.info { "$name: Throughput: ${perSecond.humanReadable()} byte/s" }
    }
}
