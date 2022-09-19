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
package de.tweerlei.plumber.pipeline

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.NullValue
import de.tweerlei.plumber.worker.types.Range
import mu.KLogging
import org.springframework.stereotype.Service
import javax.annotation.PreDestroy

@Service
class WorkerRunner {

    class InterruptibleRunContext(
        private val failFast: Boolean
    ) : Worker.RunContext {
        private var interrupted = false

        fun interrupt() {
            interrupted = true
        }

        override fun isInterrupted() =
            interrupted

        override fun isFailFast() =
            failFast
    }

    companion object : KLogging()

    private var currentThread: Thread? = null
    private var currentContext: InterruptibleRunContext? = null

    fun runWorker(
        worker: Worker,
        params: PipelineParams
    ) {
        val runContext = InterruptibleRunContext(params.failFast)
        currentThread = Thread.currentThread()
        currentContext = runContext
        try {
            worker.open(runContext).use {
                worker.process(WorkItem.of(NullValue.INSTANCE,
                    WellKnownKeys.RANGE to params.toRange()
                ))
            }
        } finally {
            currentContext = null
            currentThread = null
        }
    }

    private fun PipelineParams.toRange() =
        when {
            startAfterKey is NullValue && stopAfterKey is NullValue -> NullValue.INSTANCE
            else -> Range(
                startAfterKey,
                stopAfterKey
            )
        }

    /** called by Spring upon JVM termination */
    @PreDestroy
    fun onDestroy() {
        val danglingThread = currentThread
        if (danglingThread != null) {
            logger.warn { "Interrupting all workers" }
            currentContext?.interrupt()
//            danglingThread.interrupt()
            danglingThread.join()
        }
    }
}
