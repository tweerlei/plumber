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
package de.tweerlei.plumber.worker.impl.expr

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.types.ComparableValue
import de.tweerlei.plumber.worker.types.DoubleValue
import de.tweerlei.plumber.worker.types.LongValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TimesWorkerTest {

    @Test
    fun testLong() {
        test(LongValue(1L), LongValue(1L), LongValue(1L))
        test(LongValue(1L), LongValue(-1001L), LongValue(-1001L))
        test(LongValue(1001L), LongValue(1L), LongValue(1001L))
        test(LongValue(1001L), LongValue(1001L), LongValue(1002001L))
    }

    @Test
    fun testDouble() {
        test(DoubleValue(0.12), DoubleValue(0.23), DoubleValue(0.0276))
        test(DoubleValue(0.12), DoubleValue(1001.23), DoubleValue(120.1476))
        test(DoubleValue(1001.12), DoubleValue(0.23), DoubleValue(230.2576))
        test(DoubleValue(1001.12), DoubleValue(1001.23), DoubleValue(1002351.3776))
    }

    @Test
    fun testMixed() {
        test(LongValue(-1L), DoubleValue(0.23), DoubleValue(-0.23))
        test(DoubleValue(0.12), LongValue(1001L), DoubleValue(120.12))
        test(LongValue(1001L), DoubleValue(0.23), DoubleValue(230.23))
        test(DoubleValue(1001.12), LongValue(1001L), DoubleValue(1002121.12))
    }

    private fun test(current: ComparableValue, other: ComparableValue, expected: ComparableValue) {
        val item = TestWorkerRunner(WorkItem.from(current))
            .append { w -> TimesWorker({ other }, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        with (item.getAs<ComparableValue>()) {
            shouldBe(expected)
            getName().shouldBe(expected.getName())
        }
    }
}
