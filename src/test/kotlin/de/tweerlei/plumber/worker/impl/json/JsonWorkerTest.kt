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
package de.tweerlei.plumber.worker.impl.json

import com.fasterxml.jackson.core.JsonPointer
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.ByteArrayInputStreamProvider
import de.tweerlei.plumber.worker.impl.ByteArrayOutputStreamProvider
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import de.tweerlei.plumber.worker.impl.node.NodeGetWorker
import de.tweerlei.plumber.worker.impl.record.RecordSetWorker
import de.tweerlei.plumber.worker.types.ByteArrayValue
import de.tweerlei.plumber.worker.types.DoubleValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class JsonWorkerTest {

    @Test
    fun testJsonWithPath() {

        val json = """
            {
            "version": 1,
            "obj": {
                "string": "Hello",
                "number": 42,
                "boolean": true,
                "null": null,
                "array": [ 1,2,3 ]
                }
            }
        """
        val objectMapper = ObjectMapper()

        val item = TestWorkerRunner(WorkItem.of(ByteArrayValue.of(json.toByteArray(StandardCharsets.UTF_8))))
            .append { w -> FromJsonWorker(objectMapper, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/obj"), w) }
            .append { w -> SettingWorker(WellKnownKeys.NODE, { item -> item.get(WorkItem.DEFAULT_KEY) }, w) }
            .append { w -> ToJsonWorker(objectMapper, false, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        with (item) {
            get().toAny().shouldBe("""{"string":"Hello","number":42,"boolean":true,"null":null,"array":[1,2,3]}""")
        }
    }

    @Test
    fun testReadWrite() {

        val xml = """
            {
            "version": 1,
            "obj": {
                "string": "Hello",
                "number": 42,
                "boolean": true,
                "null": null,
                "array": [ 1,2,3 ]
                }
            }
        """
        val objectMapper = ObjectMapper()
        val output = ByteArrayOutputStreamProvider()

        val items = TestWorkerRunner(WorkItem.of())
            .append { w -> JsonReadWorker(ByteArrayInputStreamProvider(xml.toByteArray(StandardCharsets.UTF_8)), objectMapper, 10, w) }
            .append { w -> SettingWorker(WorkItem.DEFAULT_KEY, { DoubleValue.of(3.14) }, w) }
            .append { w -> RecordSetWorker("2", w) }
            .append { w -> JsonWriteWorker(output, objectMapper, null, false, w) }
            .run()

        items.size.shouldBe(3)
        output.getBytes().toString(StandardCharsets.UTF_8).shouldBe(
            """[1, 2, 3]"""
        )
    }

    @Test
    fun testReadWriteWrapped() {

        val xml = """
            {
            "version": 1,
            "obj": {
                "string": "Hello",
                "number": 42,
                "boolean": true,
                "null": null,
                "array": [ 1,2,3 ]
                }
            }
        """
        val objectMapper = ObjectMapper()
        val output = ByteArrayOutputStreamProvider()

        val items = TestWorkerRunner(WorkItem.of())
            .append { w -> JsonReadWorker(ByteArrayInputStreamProvider(xml.toByteArray(StandardCharsets.UTF_8)), objectMapper, 10, w) }
            .append { w -> SettingWorker(WorkItem.DEFAULT_KEY, { DoubleValue.of(3.14) }, w) }
            .append { w -> RecordSetWorker("2", w) }
            .append { w -> JsonWriteWorker(output, objectMapper, "items", false, w) }
            .run()

        items.size.shouldBe(3)
        output.getBytes().toString(StandardCharsets.UTF_8).shouldBe(
            """{"items":[1, 2, 3]}"""
        )
    }
}
