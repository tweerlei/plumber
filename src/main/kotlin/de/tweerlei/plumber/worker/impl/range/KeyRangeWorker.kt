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

import de.tweerlei.plumber.util.range.KeyRange
import de.tweerlei.plumber.util.range.KeyRangeGenerator
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.ComparableValue
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.NullValue
import de.tweerlei.plumber.worker.types.Range

class KeyRangeWorker(
    private val partitions: Int,
    private val keyChars: String,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        item.get(WellKnownKeys.RANGE)
            .toRange()
            .let { range ->
                generateRanges(range.startAfter, range.endWith, fn)
            }
    }

    private fun generateRanges(startAfter: ComparableValue, endWith: ComparableValue, fn: (WorkItem) -> Boolean) {
        when {
            startAfter is LongValue && endWith is LongValue -> generateNumberRanges(
                startAfter.toAny(),
                endWith.toAny(),
                fn
            )
            else -> generateStringRanges(
                startAfter.asOptional()?.toString(),
                endWith.asOptional()?.toString(),
                fn
            )
        }
    }

    private fun generateNumberRanges(minValue: Long, maxValue: Long, fn: (WorkItem) -> Boolean) {
        val n = maxValue - minValue
        if (n <= partitions) {
            for (i in minValue until maxValue) {
                fn(toWorkItem(i, i + 1))
            }
        } else {
            for (i in 0 until partitions) {
                fn(toWorkItem(minValue + i * n / partitions, minValue + (i + 1) * n / partitions))
            }
        }
    }

    private fun toWorkItem(startAfter: Long, endWith: Long) =
        WorkItem.of(
            NullValue.INSTANCE,
            WellKnownKeys.RANGE to Range.of(startAfter, endWith)
        )

    private fun generateStringRanges(startAfterKey: String?, stopAfterKey: String?, fn: (WorkItem) -> Boolean) {
        KeyRangeGenerator(keyChars)
            .generateRanges(partitions, startAfterKey, stopAfterKey)
            .all { range ->
                fn(range.toWorkItem())
            }
    }

    private fun KeyRange.toWorkItem() =
        WorkItem.of(NullValue.INSTANCE,
            WellKnownKeys.RANGE to Range.of(startAfterKey, endWithKey)
        )
}
