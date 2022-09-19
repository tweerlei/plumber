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

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.Record
import mu.KLogging
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class CsvReadWorker(
    private val file: File,
    private val csvMapper: CsvMapper,
    private val separator: Char,
    private val header: Boolean,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    private lateinit var stream: InputStream

    override fun onOpen() {
        stream = FileInputStream(file)
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        when (header) {
            true -> generateItemsWithHeader()
            false -> generateItemsWithoutHeader()
        }
            .use { reader ->
                reader.all {
                    fn(it.toWorkItem())
                }
            }
    }

    private fun generateItemsWithHeader() =
        RecordIterator(csvMapper
                .readerFor(Record::class.java)
                .with(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS)
                .with(CsvSchema.emptySchema().withColumnSeparator(separator).withHeader())
                .readValues<Record>(stream)) {
            it
        }

    private fun generateItemsWithoutHeader() =
        RecordIterator(csvMapper
                .readerFor(Array<String>::class.java)
                .with(CsvParser.Feature.WRAP_AS_ARRAY)
                .with(CsvSchema.emptySchema().withColumnSeparator(separator))
                .readValues<Array<String>>(stream)) {
            Record.from(it)
        }

    private fun Record.toWorkItem() =
        WorkItem.from(
            this,
            WellKnownKeys.PATH to file.parentFile?.absolutePath,
            WellKnownKeys.NAME to file.name
        ).also { item ->
            item.set(this, WellKnownKeys.RECORD)
        }

    override fun onClose() {
        stream.close()
    }

    private class RecordIterator<T, U>(
        val iterator: MappingIterator<T>,
        val mapper: (T) -> U
    ): Iterable<U>, Closeable {

        override fun iterator() =
            iterator.asSequence().map(mapper).iterator()

        override fun close() {
            iterator.close()
        }
    }
}
