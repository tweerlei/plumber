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
import de.tweerlei.plumber.worker.range.RangeIteratingWorker
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RangeIteratingWorkerTest {

    @Test
    fun `When increment is 1 then items are created`() {
        val items = TestWorkerRunner()
            .append { w -> SettingWorker(mapOf(
                WellKnownKeys.RANGE to Range(0L, 10L)
            ), w) }
            .append { w -> RangeIteratingWorker(1, Int.MAX_VALUE, w) }
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
            .append { w -> RangeIteratingWorker(2, Int.MAX_VALUE, w) }
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
            .append { w -> RangeIteratingWorker(-2, Int.MAX_VALUE, w) }
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
            .append { w -> RangeIteratingWorker(2, Int.MAX_VALUE, w) }
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
            .append { w -> RangeIteratingWorker(-2, Int.MAX_VALUE, w) }
            .run(WorkItem.of(""))
            .toList()

        items.size.shouldBe(0)
    }
}
