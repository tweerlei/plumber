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
import com.fasterxml.jackson.dataformat.xml.XmlMapper
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

        val item = TestWorkerRunner(WorkItem.of(ByteArrayValue.of(xml.toByteArray(StandardCharsets.UTF_8))))
            .append { w -> FromXmlWorker(objectMapper, w) }
            .append { w -> NodeGetWorker(JsonPointer.compile("/obj"), w) }
            .append { w -> SettingWorker(WellKnownKeys.NODE, { item -> item.get(WorkItem.DEFAULT_KEY) }, w) }
            .append { w -> ToXmlWorker("ROOT", objectMapper, false, w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().toAny().shouldBe(
            """<ROOT><string>Hello</string><number>42</number><boolean>true</boolean><null>null</null><array><item>1</item><item>2</item><item>3</item></array></ROOT>"""
        )
    }

    @Test
    fun testReadWrite() {

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
        val output = ByteArrayOutputStreamProvider()

        val items = TestWorkerRunner(WorkItem.of())
            .append { w -> XmlReadWorker(ByteArrayInputStreamProvider(xml.toByteArray(StandardCharsets.UTF_8)), "item", objectMapper, 10, w) }
            .append { w -> SettingWorker(WorkItem.DEFAULT_KEY, { DoubleValue.of(3.14) }, w) }
            .append { w -> RecordSetWorker("2", w) }
            .append { w -> XmlWriteWorker(output, "item", "items", objectMapper, false, w) }
            .run()

        items.size.shouldBe(3)
        output.getBytes().toString(StandardCharsets.UTF_8).shouldBe(
            """<?xml version='1.0' encoding='UTF-8'?><items><item>1</item><item>2</item><item>3</item></items>"""
        )
    }
}
