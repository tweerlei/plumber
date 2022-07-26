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
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.file.FileKeys
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import mu.KLogging
import java.io.*

class CsvReadWorker(
    private val file: File,
    private val csvMapper: CsvMapper,
    limit: Int,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    private lateinit var stream: InputStream

    override fun onOpen() {
        stream = FileInputStream(file)
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        csvMapper.readerFor(Record::class.java)
            .with(CsvSchema.emptySchema().withHeader())
            .readValues<Record>(stream)
            .let { reader ->
                try {
                    var keepGenerating = true
                    while (keepGenerating && reader.hasNext()) {
                        keepGenerating = reader.next()
                            ?.let { obj ->
                                fn(obj.toWorkItem())
                            } ?: false
                    }
                } finally {
                    reader.close()
                }
            }
    }

    private fun Record.toWorkItem() =
        WorkItem.of(
            this,
            FileKeys.FILE_PATH to file.parentFile?.absolutePath,
            FileKeys.FILE_NAME to file.name
        ).also { item ->
            item.set(this, WellKnownKeys.RECORD)
        }

    override fun onClose() {
        stream.close()
    }
}
