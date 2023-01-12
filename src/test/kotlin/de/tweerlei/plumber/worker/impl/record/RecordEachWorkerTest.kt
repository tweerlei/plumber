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
package de.tweerlei.plumber.worker.impl.record

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.NullValue
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.types.StringValue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RecordEachWorkerTest {

    @Test
    fun testEmpty() {

        val items = TestWorkerRunner(WorkItem.of(NullValue.INSTANCE,
            WellKnownKeys.RECORD to Record()
        ))
            .append { w -> RecordEachWorker(10, w) }
            .run()

        items.shouldBeEmpty()
    }

    @Test
    fun testEach() {

        val items = TestWorkerRunner(WorkItem.of(NullValue.INSTANCE,
            WellKnownKeys.RECORD to Record.of(
                "entry0" to StringValue.of("value0"),
                "entry1" to StringValue.of("value1"),
                "entry2" to StringValue.of("value2")
            )
        ))
            .append { w -> RecordEachWorker(2, w) }
            .run()
            .toList()

        items.size.shouldBe(2)
        with (items[0]) {
            get().shouldBe(StringValue.of("value0"))
            get(WellKnownKeys.NAME).shouldBe(StringValue.of("entry0"))
        }
        with (items[1]) {
            get().shouldBe(StringValue.of("value1"))
            get(WellKnownKeys.NAME).shouldBe(StringValue.of("entry1"))
        }
    }
}
