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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RecordSetWorkerTest {

    @Test
    fun testSet() {

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("value")))
            .append { w -> RecordSetWorker("entry", w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<Record>(WellKnownKeys.RECORD)["entry"].shouldBe(StringValue.of("value"))
    }

    @Test
    fun testOverwrite() {

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("value"),
            WellKnownKeys.RECORD to Record.of("entry" to StringValue.of("value2"))
        ))
            .append { w -> RecordSetWorker("entry", w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<Record>(WellKnownKeys.RECORD)["entry"].shouldBe(StringValue.of("value"))
    }

    @Test
    fun testSetNull() {

        val item = TestWorkerRunner(WorkItem.of(NullValue.INSTANCE,
            WellKnownKeys.RECORD to Record.of("entry" to StringValue.of("value"))
        ))
            .append { w -> RecordSetWorker("entry", w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<Record>(WellKnownKeys.RECORD)["entry"].shouldBe(NullValue.INSTANCE)
    }
}
