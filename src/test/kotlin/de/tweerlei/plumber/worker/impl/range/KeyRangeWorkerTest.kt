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

import de.tweerlei.plumber.worker.types.Range
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class KeyRangeWorkerTest {

    @Test
    fun `When range is numeric then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.from(100L, 200L) },
                w
            ) }
            .append { w -> KeyRangeWorker(10, "0123456789", Long.MAX_VALUE, w) }
            .run(WorkItem.from(""))
            .toList()

        items.size.shouldBe(10)
        items[0].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(100L, 110L))
        items[1].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(110L, 120L))
        items[2].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(120L, 130L))
        items[3].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(130L, 140L))
        items[4].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(140L, 150L))
        items[5].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(150L, 160L))
        items[6].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(160L, 170L))
        items[7].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(170L, 180L))
        items[8].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(180L, 190L))
        items[9].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(190L, 200L))
    }

    @Test
    fun `When range is small then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.from(100L, 105L) },
                w
            ) }
            .append { w -> KeyRangeWorker(10, "0123456789", Long.MAX_VALUE, w) }
            .run(WorkItem.from(""))
            .toList()

        items.size.shouldBe(5)
        items[0].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(100L, 101L))
        items[1].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(101L, 102L))
        items[2].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(102L, 103L))
        items[3].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(103L, 104L))
        items[4].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from(104L, 105L))
    }

    @Test
    fun `When range is keyed then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.from("abc", null) },
                w
            ) }
            .append { w -> KeyRangeWorker(10, "0123456789abcdef", Long.MAX_VALUE, w) }
            .run(WorkItem.from(""))
            .toList()

        items.size.shouldBe(10)
        items[0].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("abc", "b3"))
        items[1].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("b3", "bc"))
        items[2].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("bc", "c4"))
        items[3].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("c4", "cd"))
        items[4].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("cd", "d5"))
        items[5].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("d5", "de"))
        items[6].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("de", "e6"))
        items[7].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("e6", "ef"))
        items[8].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("ef", "f7"))
        items[9].getAs<Range>(WellKnownKeys.RANGE).shouldBe(Range.from("f7", null))
    }
}
