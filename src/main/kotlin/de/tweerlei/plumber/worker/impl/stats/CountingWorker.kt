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

import de.tweerlei.plumber.util.humanReadable
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import mu.KLogging
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class CountingWorker(
    private val name: String,
    private val interval: Int,
    worker: Worker
): DelegatingWorker(worker) {

    companion object: KLogging()

    private val sentFiles = AtomicInteger()
    private var lastTime = AtomicLong()

    override fun onOpen() {
        lastTime.set(System.currentTimeMillis())
    }

    override fun doProcess(item: WorkItem) =
        sentFiles.incrementAndGet()
            .also { counter ->
                if (counter % interval == 0) {
                    val now = System.currentTimeMillis()
                    val last = lastTime.getAndSet(now)
                    val perSecond = interval.toDouble() * 1000 / (now - last).coerceAtLeast(1)
                    logger.info { "$name: Items processed: $counter @ ${perSecond.humanReadable()} items/s" }
                }
                item.set(counter, WellKnownKeys.COUNT)
            }.let { true }

    override fun onClose() {
        logger.info { "$name: Items processed: ${sentFiles.get()}" }
    }
}
