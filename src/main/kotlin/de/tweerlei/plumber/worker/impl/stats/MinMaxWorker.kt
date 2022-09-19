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

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.types.ComparableValue
import mu.KLogging
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class MinMaxWorker(
    private val name: String,
    private val interval: Long,
    worker: Worker
): DelegatingWorker(worker) {

    companion object: KLogging()

    private val sentFiles = AtomicLong()
    private val minValue = AtomicReference<ComparableValue>()
    private val maxValue = AtomicReference<ComparableValue>()

    override fun doProcess(item: WorkItem) =
        item.getAs<ComparableValue>()
            .let { value ->
                val curMin = minValue.accumulateAndGet(value) { a, b -> minOf(a ?: b, b) }
                val curMax = maxValue.accumulateAndGet(value) { a, b -> maxOf(a ?: b, b) }
                sentFiles.incrementAndGet()
                    .also { counter ->
                        if (counter % interval == 0L) {
                            logger.info { "$name: Items processed: $counter, Min. item: $curMin, max. item: $curMax" }
                        }
                    }
            }.let { true }

    override fun onClose() {
        logger.info { "$name: Min. item: ${minValue.get()}, max. item: ${maxValue.get()}" }
    }
}
