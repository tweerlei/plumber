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

import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WrappingWorker
import de.tweerlei.plumber.worker.types.WorkItemList
import mu.KLogging
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

class BulkWorker(
    private val queueSizePerThread: Int,
    worker: Worker
): WrappingWorker(worker) {

    companion object : KLogging()

    private val accumulator = ThreadLocal<AtomicReference<WorkItemList>>()
    private val allAccumulators = ConcurrentLinkedQueue<AtomicReference<WorkItemList>>()

    private fun currentAccumulator() =
        when (val ref = accumulator.get()) {
            null -> AtomicReference<WorkItemList>()
                .also {
                    accumulator.set(it)
                    allAccumulators.add(it)
                }
            else -> ref
        }.let { ref ->
            when (val list = ref.get()) {
                null -> WorkItemList(queueSizePerThread)
                    .also { ref.set(it) }
                else -> list
            }
        }

    private fun resetAccumulator() =
        accumulator.get()?.set(null)

    private fun passOn(items: WorkItemList) {
        val nextItem = WorkItem.from(
            items,
            WellKnownKeys.WORK_ITEMS to items,
            WellKnownKeys.SIZE to items.size
        )
        passOn(nextItem)
    }

    override fun process(item: WorkItem) {
        val items = currentAccumulator()
        items.add(item)

        if (items.size >= queueSizePerThread) {
            resetAccumulator()
            passOn(items)
        }
    }

    override fun onClose() {
        allAccumulators.forEach { ref ->
            ref.get()?.let {
                passOn(it)
            }
            ref.set(null)
        }
    }
}
