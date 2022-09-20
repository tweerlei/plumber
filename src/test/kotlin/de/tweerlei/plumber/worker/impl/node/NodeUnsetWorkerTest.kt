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
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.json.FromJsonWorker
import de.tweerlei.plumber.worker.types.JsonNodeValue
import de.tweerlei.plumber.worker.types.StringValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NodeUnsetWorkerTest {

    @Test
    fun testUnset() {

        val objectMapper = ObjectMapper()

        val item = TestWorkerRunner(WorkItem.from("""{"entry":"value2"}"""))
            .append { w -> FromJsonWorker(JsonNode::class.java, objectMapper, w) }
            .append { w -> NodeUnsetWorker(JsonPointer.compile("/entry"), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<JsonNodeValue>(WellKnownKeys.NODE).toString().shouldBe("""{}""")
    }

    @Test
    fun testUnsetNonexisting() {

        val objectMapper = ObjectMapper()

        val item = TestWorkerRunner(WorkItem.from("""{"entry":"value2"}"""))
            .append { w -> FromJsonWorker(JsonNode::class.java, objectMapper, w) }
            .append { w -> NodeUnsetWorker(JsonPointer.compile("/entry2"), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<JsonNodeValue>(WellKnownKeys.NODE).toString().shouldBe("""{"entry":"value2"}""")
    }
}
