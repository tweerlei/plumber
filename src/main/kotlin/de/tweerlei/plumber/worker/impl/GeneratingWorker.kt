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
package de.tweerlei.plumber.worker.impl

import de.tweerlei.plumber.util.printStackTraceUpTo
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import mu.KLogging

abstract class GeneratingWorker(
    private val limit: Long,
    worker: Worker
): WrappingWorker(worker) {

    companion object: KLogging()

    private var count = 0L

    final override fun process(item: WorkItem) {
        generateItems(item) { newItem ->
            count++
            if (count > limit || runContext.isInterrupted())
                false
            else
                item.plus(newItem)
                    .also { nextItem ->
                        try {
                            passOn(nextItem)
                        } catch (e: Exception) {
                            if (runContext.isFailFast())
                                throw e
                            else
                                logger.error {
                                    "Error while processing item $nextItem\n" +
                                    e.printStackTraceUpTo(this::class)
                                }
                        }
                    }.let { true }
        }
    }

    protected abstract fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean)
}
