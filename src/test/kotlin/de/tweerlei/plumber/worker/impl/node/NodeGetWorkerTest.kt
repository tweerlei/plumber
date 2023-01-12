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
package de.tweerlei.plumber.worker.impl.node

import com.fasterxml.jackson.core.JsonPointer
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import de.tweerlei.plumber.worker.impl.json.FromJsonWorker
import de.tweerlei.plumber.worker.types.*
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NodeGetWorkerTest {

    @Test
    fun testGetString() {

        val objectMapper = ObjectMapper()

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("""{"entry":"value"}""")))
            .append { w -> FromJsonWorker(objectMapper, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/entry"), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().toAny().shouldBe("value")
    }

    @Test
    fun testGetBoolean() {

        val objectMapper = ObjectMapper()

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("""{"entry":true}""")))
            .append { w -> FromJsonWorker(objectMapper, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/entry"), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().toAny().shouldBe(true)
    }

    @Test
    fun testGetNumber() {

        val objectMapper = ObjectMapper()

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("""{"entry":42}""")))
            .append { w -> FromJsonWorker(objectMapper, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/entry"), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().toAny().shouldBe(42L)
    }

    @Test
    fun testGetBinary() {

        val value = byteArrayOf(42, -64, 0, 32, -128, 127)

        val item = TestWorkerRunner(WorkItem.of(ByteArrayValue.of(value)))
            .append { w -> NodeModifyWorker(JsonPointer.compile("/entry"), w) }
            .append { w -> SettingWorker(WorkItem.DEFAULT_KEY, { StringValue.of("value") }, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/entry"), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().toByteArray().contentEquals(value).shouldBeTrue()
    }

    @Test
    fun testGetNonExisting() {

        val objectMapper = ObjectMapper()

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("""{"entry2":"value"}""")))
            .append { w -> FromJsonWorker(objectMapper, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/entry"), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe(NullValue.INSTANCE)
    }
}
