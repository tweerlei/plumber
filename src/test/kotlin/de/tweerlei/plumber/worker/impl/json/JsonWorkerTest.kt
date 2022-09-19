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
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.node.NodeGetWorker
import de.tweerlei.plumber.worker.types.StringValue
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

        val item = TestWorkerRunner()
            .append { w -> FromJsonWorker(JsonNode::class.java, objectMapper, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/obj"), w) }
            .append { w -> ToJsonWorker(objectMapper, false, w) }
            .run(WorkItem.from(json.toByteArray(StandardCharsets.UTF_8)))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("""{"string":"Hello","number":42,"boolean":true,"null":null,"array":[1,2,3]}""")
    }
}
