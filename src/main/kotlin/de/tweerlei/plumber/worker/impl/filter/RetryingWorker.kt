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
import de.tweerlei.plumber.worker.impl.WrappingWorker
import mu.KLogging

class RetryingWorker(
    private val name: String,
    private val numberOfRetries: Long,
    private val retryDelaySeconds: Int,
    worker: Worker
): WrappingWorker(worker) {

    companion object: KLogging()

    override fun process(item: WorkItem) {
        for (i in 0..numberOfRetries) {
            try {
                passOn(item)
                break
            } catch (e: Exception) {
                if (i < numberOfRetries) {
                    logger.warn {
                        "$name: Error while processing item $item, retrying ${numberOfRetries - i} times"
                    }
                    if (retryDelaySeconds > 0)
                        Thread.sleep(retryDelaySeconds * 1000L)
                } else if (runContext.isFailFast()) {
                    throw e
                } else {
                    logger.error {
                        "$name: Error while processing item $item, retry limit exceeded\n" +
                                e.printStackTraceUpTo(this::class)
                    }
                }
            }
        }
    }
}
