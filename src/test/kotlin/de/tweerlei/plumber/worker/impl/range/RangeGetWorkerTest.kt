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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RangeGetWorkerTest {

    @Test
    fun testGetStart() {

        val item = TestWorkerRunner(WorkItem.of(NullValue.INSTANCE,
            WellKnownKeys.RANGE to Range.of(42L, 100L)
        ))
            .append { w -> RangeGetWorker(RangeKey.start, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe(LongValue.of(42L))
    }

    @Test
    fun testGetEnd() {

        val item = TestWorkerRunner(WorkItem.of(NullValue.INSTANCE,
            WellKnownKeys.RANGE to Range.of(42L, 100L)
        ))
            .append { w -> RangeGetWorker(RangeKey.end, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe(LongValue.of(100L))
    }
}
