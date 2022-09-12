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

class RangeIteratingWorkerTest {

    @Test
    fun `When increment is 1 then items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range(0L, 10L)
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789", 1, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(10)
        items[0].getOptional().shouldBe(1L)
        items[1].getOptional().shouldBe(2L)
        items[2].getOptional().shouldBe(3L)
        items[3].getOptional().shouldBe(4L)
        items[4].getOptional().shouldBe(5L)
        items[5].getOptional().shouldBe(6L)
        items[6].getOptional().shouldBe(7L)
        items[7].getOptional().shouldBe(8L)
        items[8].getOptional().shouldBe(9L)
        items[9].getOptional().shouldBe(10L)
    }

    @Test
    fun `When increment is 2 then items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range(0L, 10L)
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789", 2, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(5)
        items[0].getOptional().shouldBe(2L)
        items[1].getOptional().shouldBe(4L)
        items[2].getOptional().shouldBe(6L)
        items[3].getOptional().shouldBe(8L)
        items[4].getOptional().shouldBe(10L)
    }

    @Test
    fun `When increment is -2 then items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range(10L, 0L)
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789", -2, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(5)
        items[0].getOptional().shouldBe(8L)
        items[1].getOptional().shouldBe(6L)
        items[2].getOptional().shouldBe(4L)
        items[3].getOptional().shouldBe(2L)
        items[4].getOptional().shouldBe(0L)
    }

    @Test
    fun `When increment is 2 but range is decreasing then no items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range(10L, 0L)
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789", 2, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(0)
    }

    @Test
    fun `When increment is -2 but range is increasing then no items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range(0L, 10L)
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789", -2, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(0)
    }

    @Test
    fun `When increment is 1 then strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range("0", "10")
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", 1, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(16)
        items[0].getOptional().shouldBe("1")
        items[1].getOptional().shouldBe("2")
        items[2].getOptional().shouldBe("3")
        items[3].getOptional().shouldBe("4")
        items[4].getOptional().shouldBe("5")
        items[5].getOptional().shouldBe("6")
        items[6].getOptional().shouldBe("7")
        items[7].getOptional().shouldBe("8")
        items[8].getOptional().shouldBe("9")
        items[9].getOptional().shouldBe("a")
        items[10].getOptional().shouldBe("b")
        items[11].getOptional().shouldBe("c")
        items[12].getOptional().shouldBe("d")
        items[13].getOptional().shouldBe("e")
        items[14].getOptional().shouldBe("f")
        items[15].getOptional().shouldBe("10")
    }

    @Test
    fun `When increment is 2 then strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range("0", "10")
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", 2, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(8)
        items[0].getOptional().shouldBe("2")
        items[1].getOptional().shouldBe("4")
        items[2].getOptional().shouldBe("6")
        items[3].getOptional().shouldBe("8")
        items[4].getOptional().shouldBe("a")
        items[5].getOptional().shouldBe("c")
        items[6].getOptional().shouldBe("e")
        items[7].getOptional().shouldBe("10")
    }

    @Test
    fun `When increment is -2 then strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range("10", "0")
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", -2, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(8)
        items[0].getOptional().shouldBe("e")
        items[1].getOptional().shouldBe("c")
        items[2].getOptional().shouldBe("a")
        items[3].getOptional().shouldBe("8")
        items[4].getOptional().shouldBe("6")
        items[5].getOptional().shouldBe("4")
        items[6].getOptional().shouldBe("2")
        items[7].getOptional().shouldBe("0")
    }

    @Test
    fun `When increment is 2 but range is decreasing then no strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range("10", "0")
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", 2, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(0)
    }

    @Test
    fun `When increment is -2 but range is increasing then no strings are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range("0", "10")
            ), w) }
            .append { w -> RangeIteratingWorker("0123456789abcdef", -2, Long.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(0)
    }
}
