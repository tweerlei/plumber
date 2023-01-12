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
package de.tweerlei.plumber.worker.impl.range

import de.tweerlei.plumber.util.range.KeySequenceGenerator
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.Range
import de.tweerlei.plumber.worker.types.toValue
import mu.KLogging

class RangeIteratingWorker(
    private val keyChars: String,
    private val step: Long,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        item.get(WellKnownKeys.RANGE)
            .toRange()
            .let { range ->
                val startAfter = range.startAfter
                val endWith = range.endWith
                when {
                    startAfter is LongValue && endWith is LongValue -> iterate(
                        startAfter.toAny(),
                        endWith.toAny(),
                        step
                    )
                    else -> KeySequenceGenerator(keyChars).generateSequence(
                        range.startAfter.asOptional()?.toString(),
                        range.endWith.asOptional()?.toString(),
                        step
                    )
                }
            }.all {
                fn(WorkItem.of(it.toValue()))
            }
    }
    
    private fun iterate(startAfter: Long, endWith: Long, step: Long) =
        LongProgression.fromClosedRange(
            startAfter,
            endWith,
            step
        ).asSequence().drop(1)
}
