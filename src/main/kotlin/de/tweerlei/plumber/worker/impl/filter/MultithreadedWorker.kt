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
package de.tweerlei.plumber.worker.impl.filter

import de.tweerlei.plumber.util.printStackTraceUpTo
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.WrappingWorker
import de.tweerlei.plumber.worker.types.LongValue
import mu.KLogging
import java.util.concurrent.LinkedBlockingQueue

class MultithreadedWorker(
    private val name: String,
    private val numberOfThreads: Int,
    private val queueSizePerThread: Int,
    worker: Worker
): WrappingWorker(worker) {

    companion object : KLogging() {
        val endMarker = WorkItem.of()
    }

    private val blockingQueue = LinkedBlockingQueue<WorkItem>(numberOfThreads * queueSizePerThread)
    private var threads: List<Thread> = emptyList()
    private var lastError: Throwable? = null

    override fun onOpen() {
        threads = (1 .. numberOfThreads).map { workerIndex ->
            Thread({
                logger.debug("Starting thread $workerIndex")
                val workerValue = LongValue.of(workerIndex)
                while (true) {
                    val nextItem = blockingQueue.take()
                    if (nextItem === endMarker) {
                        break
                    }
                    if (!runContext.isInterrupted()) {
                        nextItem.set(workerValue, WellKnownKeys.WORKER_INDEX)
                        try {
                            passOn(nextItem)
                        } catch (e: Throwable) {
                            if (runContext.isFailFast())
                                lastError = e
                            else
                                logger.error {
                                    "$name: Error on thread $workerIndex while processing item $nextItem\n" +
                                            e.printStackTraceUpTo(this::class)
                                }
                        }
                    }
                }
                logger.debug("Exiting thread $workerIndex")
            },
            "$name-worker-$workerIndex")
        }.toList()

        threads.forEach { thread -> thread.start() }
    }

    override fun process(item: WorkItem) {
        val e = lastError
        if (e != null)
            throw e

        if (!runContext.isInterrupted())
            blockingQueue.put(item)
    }

    override fun onClose() {
        threads.forEach { _ -> blockingQueue.put(endMarker) }
        threads.forEach { thread -> thread.join() }
        threads = emptyList()
    }
}
