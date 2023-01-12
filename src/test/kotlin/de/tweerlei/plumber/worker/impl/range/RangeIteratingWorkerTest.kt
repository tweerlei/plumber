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
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.Range
import de.tweerlei.plumber.worker.types.StringValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RangeIteratingWorkerTest {

    @Test
    fun `When increment is 1 then items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of(0L, 10L) },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789", 1, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(10)
        items[0].get().shouldBe(LongValue.of(1L))
        items[1].get().shouldBe(LongValue.of(2L))
        items[2].get().shouldBe(LongValue.of(3L))
        items[3].get().shouldBe(LongValue.of(4L))
        items[4].get().shouldBe(LongValue.of(5L))
        items[5].get().shouldBe(LongValue.of(6L))
        items[6].get().shouldBe(LongValue.of(7L))
        items[7].get().shouldBe(LongValue.of(8L))
        items[8].get().shouldBe(LongValue.of(9L))
        items[9].get().shouldBe(LongValue.of(10L))
    }

    @Test
    fun `When increment is 2 then items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of(0L, 10L) },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789", 2, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(5)
        items[0].get().shouldBe(LongValue.of(2L))
        items[1].get().shouldBe(LongValue.of(4L))
        items[2].get().shouldBe(LongValue.of(6L))
        items[3].get().shouldBe(LongValue.of(8L))
        items[4].get().shouldBe(LongValue.of(10L))
    }

    @Test
    fun `When increment is -2 then items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of(10L, 0L) },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789", -2, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(5)
        items[0].get().shouldBe(LongValue.of(8L))
        items[1].get().shouldBe(LongValue.of(6L))
        items[2].get().shouldBe(LongValue.of(4L))
        items[3].get().shouldBe(LongValue.of(2L))
        items[4].get().shouldBe(LongValue.of(0L))
    }

    @Test
    fun `When increment is 2 but range is decreasing then no items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of(10L, 0L) },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789", 2, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(0)
    }

    @Test
    fun `When increment is -2 but range is increasing then no items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of(0L, 10L) },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789", -2, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(0)
    }

    @Test
    fun `When increment is 1 then strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of("0", "10") },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", 1, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(16)
        items[0].get().shouldBe(StringValue.of("1"))
        items[1].get().shouldBe(StringValue.of("2"))
        items[2].get().shouldBe(StringValue.of("3"))
        items[3].get().shouldBe(StringValue.of("4"))
        items[4].get().shouldBe(StringValue.of("5"))
        items[5].get().shouldBe(StringValue.of("6"))
        items[6].get().shouldBe(StringValue.of("7"))
        items[7].get().shouldBe(StringValue.of("8"))
        items[8].get().shouldBe(StringValue.of("9"))
        items[9].get().shouldBe(StringValue.of("a"))
        items[10].get().shouldBe(StringValue.of("b"))
        items[11].get().shouldBe(StringValue.of("c"))
        items[12].get().shouldBe(StringValue.of("d"))
        items[13].get().shouldBe(StringValue.of("e"))
        items[14].get().shouldBe(StringValue.of("f"))
        items[15].get().shouldBe(StringValue.of("10"))
    }

    @Test
    fun `When increment is 2 then strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of("0", "10") },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", 2, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(8)
        items[0].get().shouldBe(StringValue.of("2"))
        items[1].get().shouldBe(StringValue.of("4"))
        items[2].get().shouldBe(StringValue.of("6"))
        items[3].get().shouldBe(StringValue.of("8"))
        items[4].get().shouldBe(StringValue.of("a"))
        items[5].get().shouldBe(StringValue.of("c"))
        items[6].get().shouldBe(StringValue.of("e"))
        items[7].get().shouldBe(StringValue.of("10"))
    }

    @Test
    fun `When increment is -2 then strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of("10", "0") },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", -2, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(8)
        items[0].get().shouldBe(StringValue.of("e"))
        items[1].get().shouldBe(StringValue.of("c"))
        items[2].get().shouldBe(StringValue.of("a"))
        items[3].get().shouldBe(StringValue.of("8"))
        items[4].get().shouldBe(StringValue.of("6"))
        items[5].get().shouldBe(StringValue.of("4"))
        items[6].get().shouldBe(StringValue.of("2"))
        items[7].get().shouldBe(StringValue.of("0"))
    }

    @Test
    fun `When increment is 2 but range is decreasing then no strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of("10", "0") },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", 2, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(0)
    }

    @Test
    fun `When increment is -2 but range is increasing then no strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(
                WellKnownKeys.RANGE,
                { Range.of("0", "10") },
                w
            ) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", -2, Long.MAX_VALUE, w) }
            .run()
            .toList()

        items.size.shouldBe(0)
    }
}
