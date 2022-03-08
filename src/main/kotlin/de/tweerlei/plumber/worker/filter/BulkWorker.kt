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
package de.tweerlei.plumber.worker.filter

import de.tweerlei.plumber.util.printStackTraceUpTo
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.WrappingWorker
import mu.KLogging
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class BulkWorker(
    private val name: String,
    private val numberOfThreads: Int,
    private val queueSizePerThread: Int,
    worker: Worker
): WrappingWorker(worker) {

    companion object : KLogging() {
        val endMarker = WorkItem.of(null)
    }

    private val blockingQueue = LinkedBlockingQueue<WorkItem>(numberOfThreads * queueSizePerThread)
    private var threads: List<Thread> = emptyList()
    private var stop = false

    override fun onOpen() {
        threads = (1 .. numberOfThreads).map { workerIndex ->
            Thread({
                logger.debug("Starting thread")
                val items = LinkedList<WorkItem>()
                while (true) {
                    if (stop) {
                        logger.debug("Exiting thread")
                        break
                    }
                    items.clear()
                    val itemCount = blockingQueue.drainTo(items, queueSizePerThread)
                    if (itemCount > 0) {
                        val nextItem = WorkItem.of(items,
                            WellKnownKeys.WORK_ITEMS to items,
                            WellKnownKeys.WORKER_INDEX to workerIndex
                        )
                        try {
                            passOn(nextItem)
                        } catch (e: Throwable) {
                            logger.error {
                                "$name: Error while processing item $nextItem\n" +
                                        e.printStackTraceUpTo(this::class)
                            }
                        }
                    }
                }
            },
            "$name-worker-$workerIndex")
        }.toList()

        threads.forEach { thread -> thread.start() }
    }

    override fun process(item: WorkItem) {
        blockingQueue.put(item)
    }

    override fun onClose() {
        stop = true
        threads.forEach { thread -> thread.join() }
        threads = emptyList()
    }
}
