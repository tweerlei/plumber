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
package de.tweerlei.plumber.worker.csv

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkerBuilder
import de.tweerlei.plumber.worker.record.RecordGetWorker
import de.tweerlei.plumber.worker.stats.CollectingWorker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class CsvWorkerTest {

    @Test
    fun testJsonWithPath() {

        val csv = """Hello,42,true,null,"Hello,42,false,null""""
        val objectMapper = CsvMapper()

        val items = mutableListOf<WorkItem>()
        WorkerBuilder.create()
            .append { w -> FromCsvWorker(objectMapper, w) }
            .append { w -> RecordGetWorker("4", w) }
            .append { w -> FromCsvWorker(objectMapper, w) }
            .append { w -> ToCsvWorker(objectMapper, w) }
            .append { w -> CollectingWorker(items, w) }
            .build()
            .process(WorkItem.of(csv.toByteArray(StandardCharsets.UTF_8)))

        assertEquals(1, items.size)
        assertEquals("Hello,42,false,null\n", items[0].getAs<String>())
    }
}
