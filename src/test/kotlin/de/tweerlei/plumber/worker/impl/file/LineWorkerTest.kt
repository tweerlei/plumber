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
package de.tweerlei.plumber.worker.impl.file

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.ByteArrayInputStreamProvider
import de.tweerlei.plumber.worker.impl.ByteArrayOutputStreamProvider
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import de.tweerlei.plumber.worker.impl.record.RecordSetWorker
import de.tweerlei.plumber.worker.types.DoubleValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class LineWorkerTest {

    @Test
    fun testReadWrite() {

        val csv = """Column A,Column B,Column C,Column D,Column E
            |Hello,42,true,null,"Hello,42,false,null"
            |Hello,42,true,null,"Hello,42,false,null",super
            |Hello,42,true,null""".trimMargin()
        val output = ByteArrayOutputStreamProvider()

        val items = TestWorkerRunner(WorkItem.of())
            .append { w -> LineReadWorker(ByteArrayInputStreamProvider(csv.toByteArray(StandardCharsets.UTF_8)), StandardCharsets.UTF_8, 10, w) }
//            .append { w -> SettingWorker(WorkItem.DEFAULT_KEY, { DoubleValue.of(3.14) }, w) }
//            .append { w -> RecordSetWorker("2", w) }
            .append { w -> LineWriteWorker(output, StandardCharsets.UTF_8, "#\n", w) }
            .run()

        items.size.shouldBe(4)
        output.getBytes().toString(StandardCharsets.UTF_8).shouldBe(
            """Column A,Column B,Column C,Column D,Column E#
            |Hello,42,true,null,"Hello,42,false,null"#
            |Hello,42,true,null,"Hello,42,false,null",super#
            |Hello,42,true,null#
            |""".trimMargin()
        )
    }
}
