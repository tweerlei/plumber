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
import de.tweerlei.plumber.worker.InputStreamProvider
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.types.StringValue
import mu.KLogging
import java.io.Closeable
import java.io.InputStream

class CsvReadWorker(
    private val inputStreamProvider: InputStreamProvider,
    private val csvMapper: CsvMapper,
    private val separator: Char,
    private val header: Boolean,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    private lateinit var stream: InputStream

    override fun onOpen() {
        stream = inputStreamProvider.open()
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        when (header) {
            true -> generateItemsWithHeader()
            false -> generateItemsWithoutHeader()
        }
            .use { reader ->
                val filePath = StringValue.of(inputStreamProvider.getPath())
                val fileName = StringValue.of(inputStreamProvider.getName())
                reader.all {
                    fn(it.toWorkItem(filePath, fileName))
                }
            }
    }

    private fun generateItemsWithHeader() =
        RecordIterator(csvMapper
                .readerFor(Map::class.java)
                .with(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS)
                .with(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
                .with(CsvSchema.emptySchema().withColumnSeparator(separator).withHeader())
                .readValues<Map<String, String>>(stream)) {
            Record.of(it)
        }

    private fun generateItemsWithoutHeader() =
        RecordIterator(csvMapper
                .readerFor(Array<String>::class.java)
                .with(CsvParser.Feature.WRAP_AS_ARRAY)
                .with(CsvSchema.emptySchema().withColumnSeparator(separator))
                .readValues<Array<String>>(stream)) {
            Record.of(it)
        }

    private fun Record.toWorkItem(path: StringValue, name: StringValue) =
        WorkItem.of(
            this,
            WellKnownKeys.PATH to path,
            WellKnownKeys.NAME to name
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
