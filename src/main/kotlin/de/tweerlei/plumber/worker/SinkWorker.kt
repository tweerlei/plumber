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
package de.tweerlei.plumber.worker

import de.tweerlei.plumber.util.humanReadable
import mu.KLogging
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

class SinkWorker: Worker {

    companion object: KLogging()

    private val count = AtomicInteger()
    private var startTime: Long = 0

    override fun open(ctx: Worker.RunContext) =
        this.apply {
            startTime = System.currentTimeMillis()
        }

    override fun process(item: WorkItem) {
        count.incrementAndGet()
    }

    override fun close() {
        val endTime = System.currentTimeMillis()
        val duration = Duration.ofMillis(endTime - startTime)
        logger.info { "Items received: ${count.get()}" }
        logger.info { "Total processing time: ${duration.humanReadable()}" }
    }
}
