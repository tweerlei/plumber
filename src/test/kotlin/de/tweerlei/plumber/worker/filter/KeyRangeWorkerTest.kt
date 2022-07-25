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

import de.tweerlei.plumber.worker.Range
import de.tweerlei.plumber.worker.TestWorkerRunner
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.attribute.SettingWorker
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class KeyRangeWorkerTest {

    @Test
    fun `When range is numeric then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range(100L, 200L)
            ), w) }
            .append { w -> KeyRangeWorker(10, null, null, null, Int.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(10)
        items[0].getOptional(WellKnownKeys.RANGE).shouldBe(Range(100L, 110L))
        items[1].getOptional(WellKnownKeys.RANGE).shouldBe(Range(110L, 120L))
        items[2].getOptional(WellKnownKeys.RANGE).shouldBe(Range(120L, 130L))
        items[3].getOptional(WellKnownKeys.RANGE).shouldBe(Range(130L, 140L))
        items[4].getOptional(WellKnownKeys.RANGE).shouldBe(Range(140L, 150L))
        items[5].getOptional(WellKnownKeys.RANGE).shouldBe(Range(150L, 160L))
        items[6].getOptional(WellKnownKeys.RANGE).shouldBe(Range(160L, 170L))
        items[7].getOptional(WellKnownKeys.RANGE).shouldBe(Range(170L, 180L))
        items[8].getOptional(WellKnownKeys.RANGE).shouldBe(Range(180L, 190L))
        items[9].getOptional(WellKnownKeys.RANGE).shouldBe(Range(190L, 200L))
    }

    @Test
    fun `When range is small then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range(100, 105)
            ), w) }
            .append { w -> KeyRangeWorker(10, null, null, null, Int.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(5)
        items[0].getOptional(WellKnownKeys.RANGE).shouldBe(Range(100L, 101L))
        items[1].getOptional(WellKnownKeys.RANGE).shouldBe(Range(101L, 102L))
        items[2].getOptional(WellKnownKeys.RANGE).shouldBe(Range(102L, 103L))
        items[3].getOptional(WellKnownKeys.RANGE).shouldBe(Range(103L, 104L))
        items[4].getOptional(WellKnownKeys.RANGE).shouldBe(Range(104L, 105L))
    }

    @Test
    fun `When range is keyed then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> KeyRangeWorker(10, "0123456789abcdef", "abc", null, Int.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(10)
        items[0].getOptional(WellKnownKeys.RANGE).shouldBe(Range("abc", "b3"))
        items[1].getOptional(WellKnownKeys.RANGE).shouldBe(Range("b3", "bc"))
        items[2].getOptional(WellKnownKeys.RANGE).shouldBe(Range("bc", "c4"))
        items[3].getOptional(WellKnownKeys.RANGE).shouldBe(Range("c4", "cd"))
        items[4].getOptional(WellKnownKeys.RANGE).shouldBe(Range("cd", "d5"))
        items[5].getOptional(WellKnownKeys.RANGE).shouldBe(Range("d5", "de"))
        items[6].getOptional(WellKnownKeys.RANGE).shouldBe(Range("de", "e6"))
        items[7].getOptional(WellKnownKeys.RANGE).shouldBe(Range("e6", "ef"))
        items[8].getOptional(WellKnownKeys.RANGE).shouldBe(Range("ef", "f7"))
        items[9].getOptional(WellKnownKeys.RANGE).shouldBe(Range("f7", null))
    }
}
