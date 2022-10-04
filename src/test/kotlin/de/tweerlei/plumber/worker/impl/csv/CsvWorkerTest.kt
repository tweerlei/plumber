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
package de.tweerlei.plumber.worker.impl.csv

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.ByteArrayInputStreamProvider
import de.tweerlei.plumber.worker.impl.ByteArrayOutputStreamProvider
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import de.tweerlei.plumber.worker.impl.record.RecordGetWorker
import de.tweerlei.plumber.worker.impl.record.RecordSetWorker
import de.tweerlei.plumber.worker.types.ByteArrayValue
import de.tweerlei.plumber.worker.types.DoubleValue
import de.tweerlei.plumber.worker.types.StringValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class CsvWorkerTest {

    @Test
    fun testFromTo() {

        val csv = """Hello,42,true,null,"Hello,42,false,null""""
        val objectMapper = CsvMapper()

        val item = TestWorkerRunner(WorkItem.of(ByteArrayValue.of(csv.toByteArray(StandardCharsets.UTF_8))))
            .append { w -> FromCsvWorker(objectMapper, ',', w) }
            .append { w -> RecordGetWorker("4", w) }
            .append { w -> FromCsvWorker(objectMapper, ',', w) }
            .append { w -> ToCsvWorker(objectMapper, ',', w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("Hello,42,false,null\n")
    }

    @Test
    fun testReadWrite() {

        val csv = """Column A,Column B,Column C,Column D,Column E
            |Hello,42,true,null,"Hello,42,false,null"
            |Hello,42,true,null,"Hello,42,false,null",super
            |Hello,42,true,null""".trimMargin()
        val objectMapper = CsvMapper()
        val output = ByteArrayOutputStreamProvider()

        val items = TestWorkerRunner(WorkItem.of())
            .append { w -> CsvReadWorker(ByteArrayInputStreamProvider(csv.toByteArray(StandardCharsets.UTF_8)), objectMapper, ',', false, 10, w) }
            .append { w -> SettingWorker(WorkItem.DEFAULT_KEY, { DoubleValue.of(3.14) }, w) }
            .append { w -> RecordSetWorker("2", w) }
            .append { w -> CsvWriteWorker(output, objectMapper, ',', false, w) }
            .run()

        items.size.shouldBe(4)
        output.getBytes().toString(StandardCharsets.UTF_8).shouldBe(
            """"Column A","Column B",3.14,"Column D","Column E"
            |Hello,42,3.14,null,"Hello,42,false,null"
            |Hello,42,3.14,null,"Hello,42,false,null",super
            |Hello,42,3.14,null,
            |""".trimMargin()
        )
    }

    @Test
    fun testReadWriteWithHeader() {

        val csv = """Column A,Column B,Column C,Column D,Column E
            |Hello,42,true,null,"Hello,42,false,null"
            |Hello,42,true,null,"Hello,42,false,null",super
            |Hello,42,true,null""".trimMargin()
        val objectMapper = CsvMapper()
        val output = ByteArrayOutputStreamProvider()

        val items = TestWorkerRunner(WorkItem.of())
            .append { w -> CsvReadWorker(ByteArrayInputStreamProvider(csv.toByteArray(StandardCharsets.UTF_8)), objectMapper, ',', true, 10, w) }
            .append { w -> SettingWorker(WorkItem.DEFAULT_KEY, { DoubleValue.of(3.14) }, w) }
            .append { w -> RecordSetWorker("Column C", w) }
            .append { w -> CsvWriteWorker(output, objectMapper, ',', true, w) }
            .run()

        items.size.shouldBe(3)
        output.getBytes().toString(StandardCharsets.UTF_8).shouldBe(
            """"Column A","Column B","Column C","Column D","Column E"
            |Hello,42,3.14,null,"Hello,42,false,null"
            |Hello,42,3.14,null,"Hello,42,false,null"
            |Hello,42,3.14,null,null
            |""".trimMargin()
        )
    }
}
