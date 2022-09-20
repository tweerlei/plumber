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

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.*
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class InRangeWorkerTest {

    @Test
    fun testNull() {
        test(NullValue.INSTANCE, NullValue.INSTANCE, NullValue.INSTANCE, false)
        test(NullValue.INSTANCE, LongValue(0L), LongValue(100L), false)
    }

    @Test
    fun testLong() {
        test(LongValue(1L), NullValue.INSTANCE, NullValue.INSTANCE, true)
        test(LongValue(1L), NullValue.INSTANCE, LongValue(100L), true)
        test(LongValue(1L), LongValue(0L), NullValue.INSTANCE, true)
        test(LongValue(1L), LongValue(0L), LongValue(100L), true)
        test(LongValue(1L), LongValue(100L), LongValue(0L), true)

        test(LongValue(-1L), LongValue(0L), LongValue(100L), false)
        test(LongValue(-1L), LongValue(100L), LongValue(0L), false)
    }

    @Test
    fun testDouble() {
        test(DoubleValue(1.0), NullValue.INSTANCE, NullValue.INSTANCE, true)
        test(DoubleValue(1.0), NullValue.INSTANCE, DoubleValue(100.0), true)
        test(DoubleValue(1.0), DoubleValue(0.0), NullValue.INSTANCE, true)
        test(DoubleValue(1.0), DoubleValue(0.0), DoubleValue(100.0), true)
        test(DoubleValue(1.0), DoubleValue(100.0), DoubleValue(0.0), true)

        test(DoubleValue(-1.0), DoubleValue(0.0), DoubleValue(100.0), false)
        test(DoubleValue(-1.0), DoubleValue(100.0), DoubleValue(0.0), false)
    }

    @Test
    fun testString() {
        test(StringValue("c"), NullValue.INSTANCE, NullValue.INSTANCE, true)
        test(StringValue("c"), NullValue.INSTANCE, StringValue("z"), true)
        test(StringValue("c"), StringValue("abc"), NullValue.INSTANCE, true)
        test(StringValue("c"), StringValue("abc"), StringValue("z"), true)
        test(StringValue("c"), StringValue("z"), StringValue("abc"), true)

        test(StringValue("C"), StringValue("abc"), StringValue("z"), false)
        test(StringValue("C"), StringValue("z"), StringValue("abc"), false)
    }

    private fun test(value: ComparableValue, lower: ComparableValue, upper: ComparableValue, result: Boolean) {

        val item = TestWorkerRunner(WorkItem.of(value,
            WellKnownKeys.RANGE to Range(lower, upper)
        ))
            .append { w -> InRangeWorker(w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<BooleanValue>().value.shouldBe(result)
    }
}
