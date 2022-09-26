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

class MinusWorkerTest {

    @Test
    fun testLong() {
        test(LongValue.of(0L), LongValue.of(0L), LongValue.of(0L))
        test(LongValue.of(0L), LongValue.of(1001L), LongValue.of(-1001L))
        test(LongValue.of(1001L), LongValue.of(0L), LongValue.of(1001L))
        test(LongValue.of(1001L), LongValue.of(1001L), LongValue.of(0L))
    }

    @Test
    fun testDouble() {
        test(DoubleValue.of(0.12), DoubleValue.of(0.23), DoubleValue.of(-0.11))
        test(DoubleValue.of(0.12), DoubleValue.of(1001.23), DoubleValue.of(-1001.11))
        test(DoubleValue.of(1001.12), DoubleValue.of(0.23), DoubleValue.of(1000.89))
        test(DoubleValue.of(1001.12), DoubleValue.of(1001.23), DoubleValue.of(-0.11))
    }

    @Test
    fun testMixed() {
        test(LongValue.of(0L), DoubleValue.of(0.23), DoubleValue.of(-0.23))
        test(DoubleValue.of(0.12), LongValue.of(1001L), DoubleValue.of(-1000.88))
        test(LongValue.of(1001L), DoubleValue.of(0.23), DoubleValue.of(1000.77))
        test(DoubleValue.of(1001.12), LongValue.of(1001L), DoubleValue.of(0.12))
    }

    private fun test(current: ComparableValue, other: ComparableValue, expected: ComparableValue) {
        val item = TestWorkerRunner(WorkItem.of(current))
            .append { w -> MinusWorker({ other }, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        with (item.getAs<ComparableValue>()) {
            shouldBe(expected)
            getName().shouldBe(expected.getName())
        }
    }
}
