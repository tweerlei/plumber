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

import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import de.tweerlei.plumber.worker.types.Range
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class KeyRangeWorkerTest {

    @Test
    fun `When range is numeric then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of(100L, 200L) },
                w
            ) }
            .append { w -> KeyRangeWorker(10, "0123456789", Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(10)
        items[0].get(WellKnownKeys.RANGE).shouldBe(Range.of(100L, 110L))
        items[1].get(WellKnownKeys.RANGE).shouldBe(Range.of(110L, 120L))
        items[2].get(WellKnownKeys.RANGE).shouldBe(Range.of(120L, 130L))
        items[3].get(WellKnownKeys.RANGE).shouldBe(Range.of(130L, 140L))
        items[4].get(WellKnownKeys.RANGE).shouldBe(Range.of(140L, 150L))
        items[5].get(WellKnownKeys.RANGE).shouldBe(Range.of(150L, 160L))
        items[6].get(WellKnownKeys.RANGE).shouldBe(Range.of(160L, 170L))
        items[7].get(WellKnownKeys.RANGE).shouldBe(Range.of(170L, 180L))
        items[8].get(WellKnownKeys.RANGE).shouldBe(Range.of(180L, 190L))
        items[9].get(WellKnownKeys.RANGE).shouldBe(Range.of(190L, 200L))
    }

    @Test
    fun `When range is small then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of(100L, 105L) },
                w
            ) }
            .append { w -> KeyRangeWorker(10, "0123456789", Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(5)
        items[0].get(WellKnownKeys.RANGE).shouldBe(Range.of(100L, 101L))
        items[1].get(WellKnownKeys.RANGE).shouldBe(Range.of(101L, 102L))
        items[2].get(WellKnownKeys.RANGE).shouldBe(Range.of(102L, 103L))
        items[3].get(WellKnownKeys.RANGE).shouldBe(Range.of(103L, 104L))
        items[4].get(WellKnownKeys.RANGE).shouldBe(Range.of(104L, 105L))
    }

    @Test
    fun `When range is keyed then partitions are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of("abc", null) },
                w
            ) }
            .append { w -> KeyRangeWorker(10, "0123456789abcdef", Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(10)
        items[0].get(WellKnownKeys.RANGE).shouldBe(Range.of("abc", "b3"))
        items[1].get(WellKnownKeys.RANGE).shouldBe(Range.of("b3", "bc"))
        items[2].get(WellKnownKeys.RANGE).shouldBe(Range.of("bc", "c4"))
        items[3].get(WellKnownKeys.RANGE).shouldBe(Range.of("c4", "cd"))
        items[4].get(WellKnownKeys.RANGE).shouldBe(Range.of("cd", "d5"))
        items[5].get(WellKnownKeys.RANGE).shouldBe(Range.of("d5", "de"))
        items[6].get(WellKnownKeys.RANGE).shouldBe(Range.of("de", "e6"))
        items[7].get(WellKnownKeys.RANGE).shouldBe(Range.of("e6", "ef"))
        items[8].get(WellKnownKeys.RANGE).shouldBe(Range.of("ef", "f7"))
        items[9].get(WellKnownKeys.RANGE).shouldBe(Range.of("f7", null))
    }
}
