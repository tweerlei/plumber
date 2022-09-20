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
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DivideWorkerTest {

    @Test
    fun testLong() {
        test(LongValue(1L), LongValue(1L), LongValue(1L))
        test(LongValue(-1001L), LongValue(1L), LongValue(-1001L))
        test(LongValue(1001L), LongValue(1L), LongValue(1001L))
        test(LongValue(1002001L), LongValue(1001L), LongValue(1001L))

        test(LongValue(1L), LongValue(0L), DoubleValue(Double.POSITIVE_INFINITY))
        test(LongValue(-1L), LongValue(0L), DoubleValue(Double.NEGATIVE_INFINITY))
        test(LongValue(0L), LongValue(0L), DoubleValue(Double.NaN))
    }

    @Test
    fun testDouble() {
        test(DoubleValue(0.12), DoubleValue(1.2), DoubleValue(0.1))
        test(DoubleValue(1001.23), DoubleValue(0.1), DoubleValue(10012.3))
        test(DoubleValue(-1001.12), DoubleValue(-0.5), DoubleValue(2002.24))
        test(DoubleValue(1001.12), DoubleValue(500.56), DoubleValue(2.0))

        test(DoubleValue(1.0), DoubleValue(0.0), DoubleValue(Double.POSITIVE_INFINITY))
        test(DoubleValue(-1.0), DoubleValue(0.0), DoubleValue(Double.NEGATIVE_INFINITY))
        test(DoubleValue(0.0), DoubleValue(0.0), DoubleValue(Double.NaN))
    }

    @Test
    fun testMixed() {
        test(LongValue(-1L), DoubleValue(2.5), DoubleValue(-0.4))
        test(DoubleValue(0.12), LongValue(2L), DoubleValue(0.06))
        test(LongValue(-1001L), DoubleValue(-2.5), DoubleValue(400.4))
        test(DoubleValue(1001.12), LongValue(2L), DoubleValue(500.56))

        test(LongValue(1L), DoubleValue(0.0), DoubleValue(Double.POSITIVE_INFINITY))
        test(DoubleValue(-1.0), LongValue(0L), DoubleValue(Double.NEGATIVE_INFINITY))
        test(DoubleValue(0.0), LongValue(0L), DoubleValue(Double.NaN))
    }

    private fun test(current: ComparableValue, other: ComparableValue, expected: ComparableValue) {
        val item = TestWorkerRunner(WorkItem.from(current))
            .append { w -> DivideWorker({ other }, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        with (item.getAs<ComparableValue>()) {
            getName().shouldBe(expected.getName())
            when {
                expected is DoubleValue && expected.value.isNaN() -> toNumber().toDouble().shouldBeNaN()
                else -> shouldBe(expected)
            }
        }
    }
}
