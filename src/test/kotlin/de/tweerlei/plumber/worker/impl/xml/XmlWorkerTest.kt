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
package de.tweerlei.plumber.worker.impl.xml

import com.fasterxml.jackson.core.JsonPointer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.node.NodeGetWorker
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class XmlWorkerTest {

    @Test
    fun testJsonWithPath() {

        val xml = """
            <root>
            <version>1</version>
            <obj>
                <string>Hello</string>
                <number>42</number>
                <boolean>true</boolean>
                <null>null</null>
                <array>
                    <item>1</item>
                    <item>2</item>
                    <item>3</item>
                </array>
            </obj>
            </root>
        """
        val objectMapper = XmlMapper()

        val item = TestWorkerRunner()
            .append { w -> FromXmlWorker(JsonNode::class.java, objectMapper, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/obj"), w) }
            .append { w -> ToXmlWorker("ROOT", objectMapper, false, w) }
            .run(WorkItem.of(xml.toByteArray(StandardCharsets.UTF_8)))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<String>().shouldBe("""<ROOT><string>Hello</string><number>42</number><boolean>true</boolean><null>null</null><array><item>1</item><item>2</item><item>3</item></array></ROOT>""")
    }
}
