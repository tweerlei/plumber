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
package de.tweerlei.plumber.worker.impl.math

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
        test(LongValue.of(1L), LongValue.of(1L), LongValue.of(1L))
        test(LongValue.of(-1001L), LongValue.of(1L), LongValue.of(-1001L))
        test(LongValue.of(1001L), LongValue.of(1L), LongValue.of(1001L))
        test(LongValue.of(1002001L), LongValue.of(1001L), LongValue.of(1001L))

        test(LongValue.of(1L), LongValue.of(0L), LongValue.of(Long.MAX_VALUE))
        test(LongValue.of(-1L), LongValue.of(0L), LongValue.of(Long.MIN_VALUE))
        test(LongValue.of(0L), LongValue.of(0L), LongValue.of(0L))
    }

    @Test
    fun testDouble() {
        test(DoubleValue.of(0.12), DoubleValue.of(1.2), DoubleValue.of(0.1))
        test(DoubleValue.of(1001.23), DoubleValue.of(0.1), DoubleValue.of(10012.3))
        test(DoubleValue.of(-1001.12), DoubleValue.of(-0.5), DoubleValue.of(2002.24))
        test(DoubleValue.of(1001.12), DoubleValue.of(500.56), DoubleValue.of(2.0))

        test(DoubleValue.of(1.0), DoubleValue.of(0.0), DoubleValue.of(Double.POSITIVE_INFINITY))
        test(DoubleValue.of(-1.0), DoubleValue.of(0.0), DoubleValue.of(Double.NEGATIVE_INFINITY))
        test(DoubleValue.of(0.0), DoubleValue.of(0.0), DoubleValue.of(Double.NaN))
    }

    @Test
    fun testMixed() {
        test(LongValue.of(-1L), DoubleValue.of(2.5), DoubleValue.of(-0.4))
        test(DoubleValue.of(0.12), LongValue.of(2L), DoubleValue.of(0.06))
        test(LongValue.of(-1001L), DoubleValue.of(-2.5), DoubleValue.of(400.4))
        test(DoubleValue.of(1001.12), LongValue.of(2L), DoubleValue.of(500.56))

        test(LongValue.of(1L), DoubleValue.of(0.0), DoubleValue.of(Double.POSITIVE_INFINITY))
        test(DoubleValue.of(-1.0), LongValue.of(0L), DoubleValue.of(Double.NEGATIVE_INFINITY))
        test(DoubleValue.of(0.0), LongValue.of(0L), DoubleValue.of(Double.NaN))
    }

    private fun test(current: ComparableValue, other: ComparableValue, expected: ComparableValue) {
        val item = TestWorkerRunner(WorkItem.of(current))
            .append { w -> DivideWorker({ other }, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        with (item.get()) {
            getName().shouldBe(expected.getName())
            when {
                expected is DoubleValue && expected.toAny().isNaN() -> toDouble().shouldBeNaN()
                else -> shouldBe(expected)
            }
        }
    }
}
