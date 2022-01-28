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

import de.tweerlei.plumber.util.KeyRange
import de.tweerlei.plumber.util.KeyRangeGenerator
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.GeneratingWorker
import de.tweerlei.plumber.worker.Worker

class KeyRangeWorker(
    private val partitions: Int,
    private val keyChars: String?,
    private val startAfterKey: String?,
    private val stopAfterKey: String?,
    limit: Int,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        when {
            item.has(WellKnownKeys.START_AFTER_KEY) && item.has(WellKnownKeys.END_WITH_KEY) ->
                generateNumberRanges(item, fn)
            else ->
                generateStringRanges(fn)
        }
    }

    private fun generateNumberRanges(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val minValue = item.getLong(WellKnownKeys.START_AFTER_KEY)
        val maxValue = item.getLong(WellKnownKeys.END_WITH_KEY)
        val n = maxValue - minValue
        if (n <= partitions) {
            for (i in minValue until maxValue) {
                fn(toWorkItem(i, i + 1))
            }
        } else {
            for (i in 1 until partitions) {
                fn(toWorkItem(i * n / partitions, (i + 1) * n / partitions))
            }
        }
    }

    private fun toWorkItem(startAfter: Long, endWith: Long) =
        WorkItem.of(
            null,
            WellKnownKeys.START_AFTER_KEY to startAfter,
            WellKnownKeys.END_WITH_KEY to endWith
        )

    private fun generateStringRanges(fn: (WorkItem) -> Boolean) {
        KeyRangeGenerator(keyChars)
            .generateRanges(partitions, startAfterKey, stopAfterKey)
            .all { range ->
                fn(range.toWorkItem())
            }
    }

    private fun KeyRange.toWorkItem() =
        WorkItem.of(startAfterKey.orEmpty())
            .apply {
                set(startAfterKey, WellKnownKeys.START_AFTER_KEY)
                set(endWithKey, WellKnownKeys.END_WITH_KEY)
            }
}
