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

class ModuloWorkerTest {

    @Test
    fun testLong() {
        test(LongValue.of(1L), LongValue.of(2L), LongValue.of(1L))
        test(LongValue.of(-1001L), LongValue.of(10L), LongValue.of(-1L))
        test(LongValue.of(1001L), LongValue.of(3L), LongValue.of(2L))
        test(LongValue.of(0L), LongValue.of(1L), LongValue.of(0L))

        test(LongValue.of(1L), LongValue.of(0L), DoubleValue.NAN)
        test(LongValue.of(-1L), LongValue.of(0L), DoubleValue.NAN)
        test(LongValue.of(0L), LongValue.of(0L), DoubleValue.NAN)
    }

    @Test
    fun testDouble() {
        test(DoubleValue.of(0.12), DoubleValue.of(1.2), DoubleValue.of(0.12))
        test(DoubleValue.of(1001.23), DoubleValue.of(2.1), DoubleValue.of(1.63))
        test(DoubleValue.of(-1001.12), DoubleValue.of(-0.5), DoubleValue.of(-0.12))
        test(DoubleValue.of(1001.12), DoubleValue.of(500.56), DoubleValue.of(0.0))

        test(DoubleValue.of(1.0), DoubleValue.of(0.0), DoubleValue.NAN)
        test(DoubleValue.of(-1.0), DoubleValue.of(0.0), DoubleValue.NAN)
        test(DoubleValue.of(0.0), DoubleValue.of(0.0), DoubleValue.NAN)
    }

    @Test
    fun testMixed() {
        test(LongValue.of(-1L), DoubleValue.of(2.5), DoubleValue.of(-1.0))
        test(DoubleValue.of(0.12), LongValue.of(2L), DoubleValue.of(0.12))
        test(LongValue.of(-1001L), DoubleValue.of(-2.5), DoubleValue.of(-1.0))
        test(DoubleValue.of(1001.12), LongValue.of(2L), DoubleValue.of(1.12))

        test(LongValue.of(1L), DoubleValue.of(0.0), DoubleValue.NAN)
        test(DoubleValue.of(-1.0), LongValue.of(0L), DoubleValue.NAN)
        test(DoubleValue.of(0.0), LongValue.of(0L), DoubleValue.NAN)
    }

    private fun test(current: ComparableValue, other: ComparableValue, expected: ComparableValue) {
        val item = TestWorkerRunner(WorkItem.of(current))
            .append { w -> ModuloWorker({ other }, w) }
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
