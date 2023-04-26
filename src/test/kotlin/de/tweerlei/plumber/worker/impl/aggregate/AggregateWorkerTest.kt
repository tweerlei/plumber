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
package de.tweerlei.plumber.worker.impl.aggregate

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import de.tweerlei.plumber.worker.impl.filter.CollectionWorker
import de.tweerlei.plumber.worker.impl.record.RecordGetWorker
import de.tweerlei.plumber.worker.types.DoubleValue
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.types.StringValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AggregateWorkerTest {

    @Test
    fun `When processing items Then aggregations are calculated per group`() {
        val allItems = listOf(
            WorkItem.of(StringValue.of("group one"),
                WellKnownKeys.RECORD to Record.of(
                    "a" to LongValue.of(1),
                    "b" to LongValue.of(2),
                    "c" to LongValue.of(3),
                    "d" to LongValue.of(4),
                )
            ),
            WorkItem.of(StringValue.of("group one"),
                WellKnownKeys.RECORD to Record.of(
                    "a" to LongValue.of(5),
                    "b" to LongValue.of(6),
                    "c" to LongValue.of(7),
                    "d" to LongValue.of(8),
                )
            ),
            WorkItem.of(StringValue.of("group two"),
                WellKnownKeys.RECORD to Record.of(
                    "a" to LongValue.of(8),
                    "b" to LongValue.of(7),
                    "c" to LongValue.of(6),
                    "d" to LongValue.of(5),
                )
            ),
            WorkItem.of(StringValue.of("group two"),
                WellKnownKeys.RECORD to Record.of(
                    "a" to LongValue.of(4),
                    "b" to LongValue.of(3),
                    "c" to LongValue.of(2),
                    "d" to LongValue.of(1),
                )
            ),
        )

        val items = TestWorkerRunner()
            .append { w -> CollectionWorker(allItems, allItems.size.toLong(), w) }
            .append { w -> GroupingWorker(w) }
            .append { w -> CountingWorker("all", Long.MAX_VALUE, w) }
            .append { w -> RecordGetWorker("a", w) }
            .append { w -> MinWorker("a", Long.MAX_VALUE, w) }
            .append { w -> RecordGetWorker("b", w) }
            .append { w -> MaxWorker("b", Long.MAX_VALUE, w) }
            .append { w -> RecordGetWorker("c", w) }
            .append { w -> SummingWorker("c", Long.MAX_VALUE, w) }
            .append { w -> RecordGetWorker("d", w) }
            .append { w -> AveragingWorker("d", Long.MAX_VALUE, w) }
            .append { w -> LastWorker(w) }
            .run()

        items.size.shouldBe(2)
        with (items.first()) {
            get(WellKnownKeys.GROUP).shouldBe(StringValue.of("group one"))
            get(WellKnownKeys.COUNT).shouldBe(LongValue.of(2))
            get(WellKnownKeys.MIN).shouldBe(LongValue.of(1))
            get(WellKnownKeys.MAX).shouldBe(LongValue.of(6))
            get(WellKnownKeys.SUM).shouldBe(LongValue.of(10))
            get(WellKnownKeys.AVG).shouldBe(DoubleValue.of(6.0))
        }
        with (items.last()) {
            get(WellKnownKeys.GROUP).shouldBe(StringValue.of("group two"))
            get(WellKnownKeys.COUNT).shouldBe(LongValue.of(2))
            get(WellKnownKeys.MIN).shouldBe(LongValue.of(4))
            get(WellKnownKeys.MAX).shouldBe(LongValue.of(7))
            get(WellKnownKeys.SUM).shouldBe(LongValue.of(8))
            get(WellKnownKeys.AVG).shouldBe(DoubleValue.of(3.0))
        }
    }
}
