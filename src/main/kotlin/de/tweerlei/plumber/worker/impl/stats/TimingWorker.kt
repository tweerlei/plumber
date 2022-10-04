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
import de.tweerlei.plumber.util.printStackTraceUpTo
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WrappingWorker
import mu.KLogging
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class TimingWorker(
    private val name: String,
    private val interval: Long,
    worker: Worker
): WrappingWorker(worker) {

    companion object: KLogging()

    private val activeWorkers = AtomicInteger()
    private val successfulFiles = AtomicLong()
    private val failedFiles = AtomicLong()
    private val totalProcessingTime = AtomicLong()

    override fun process(item: WorkItem) {
        val active = activeWorkers.incrementAndGet()
        val stopwatch = Stopwatch()
        var succ: Long
        var fail: Long
        try {
            passOn(item)
            succ = successfulFiles.incrementAndGet()
            fail = failedFiles.get()
        } catch (e: Exception) {
            if (runContext.isFailFast())
                throw e

            logger.error {
                "$name: failed to process $item\n" +
                e.printStackTraceUpTo(this::class)
            }
            succ = successfulFiles.get()
            fail = failedFiles.incrementAndGet()
        } finally {
            totalProcessingTime.addAndGet(stopwatch.elapsedMillis())
            activeWorkers.decrementAndGet()
        }

        if ((succ + fail) % interval == 0L) {
            val perItem = stopwatch.perItem((succ + fail).toDouble())
            logger.info {
                "$name: $succ / ${succ + fail} ($active active) @ ${perItem.humanReadable()} ms/item"
            }
        }
    }

    override fun onClose() {
        logger.info { "$name: Items successful: ${successfulFiles.get()}" }
        logger.info { "$name: Items failed: ${failedFiles.get()}" }
        if (successfulFiles.get() + failedFiles.get() > 0) {
            val perItem = totalProcessingTime.get().toDouble() / (successfulFiles.get() + failedFiles.get())
            logger.info {
                "$name: Net processing time per item: " +
                "${perItem.humanReadable()} ms"
            }
        }
    }
}
