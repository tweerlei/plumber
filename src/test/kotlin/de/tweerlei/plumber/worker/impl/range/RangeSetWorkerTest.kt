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
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.NullValue
import de.tweerlei.plumber.worker.types.Range
import de.tweerlei.plumber.worker.types.StringValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RangeSetWorkerTest {

    @Test
    fun testSetStart() {

        val item = TestWorkerRunner(WorkItem.of(StringValue("value")))
            .append { w -> RangeSetWorker(RangeKey.start, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        with (item.getAs<Range>(WellKnownKeys.RANGE)) {
            startAfter.shouldBe(StringValue("value"))
            endWith.shouldBe(NullValue.INSTANCE)
        }
    }

    @Test
    fun testSetEnd() {

        val item = TestWorkerRunner(WorkItem.of(StringValue("value")))
            .append { w -> RangeSetWorker(RangeKey.end, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        with (item.getAs<Range>(WellKnownKeys.RANGE)) {
            startAfter.shouldBe(NullValue.INSTANCE)
            endWith.shouldBe(StringValue("value"))
        }
    }

    @Test
    fun testOverwrite() {

        val item = TestWorkerRunner(WorkItem.of(StringValue("value"),
            WellKnownKeys.RANGE to Range(LongValue(42L), LongValue(100L))
        ))
            .append { w -> RangeSetWorker(RangeKey.start, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        with (item.getAs<Range>(WellKnownKeys.RANGE)) {
            startAfter.shouldBe(StringValue("value"))
            endWith.shouldBe(LongValue(100L))
        }
    }
}
