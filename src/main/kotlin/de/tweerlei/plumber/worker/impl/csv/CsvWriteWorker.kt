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

import com.fasterxml.jackson.databind.SequenceWriter
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.Record
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CsvWriteWorker(
    private val file: File,
    private val csvMapper: CsvMapper,
    private val separator: Char,
    private val header: Boolean,
    worker: Worker
): DelegatingWorker(worker) {

    private lateinit var stream: OutputStream
    private var writer: RecordWriter<Record, out Any>? = null

    override fun onOpen() {
        stream = FileOutputStream(file)
    }

    override fun doProcess(item: WorkItem): Boolean =
        item.getFirstAs<Record>(WellKnownKeys.RECORD)
            .let { obj ->
                writerFor(obj).write(obj)
            }.let { true }

    private fun writerFor(rec: Record) =
        when (val w = writer) {
            null -> createWriter(rec).also { writer = it }
            else -> w
        }

    private fun createWriter(rec: Record) =
        when (header) {
            true -> writerWithHeader(rec)
            false -> writerWithoutHeader(rec)
        }

    override fun onClose() {
        writer?.close()
    }

    private fun writerWithHeader(rec: Record) =
        RecordWriter<Record, Iterable<Any>>(csvMapper
                .writerFor(Iterable::class.java)
                // See https://github.com/FasterXML/jackson-dataformats-text/issues/10
                // withNullValue() does not apply to arrays and collections
                .with(rec.toFormatSchema().withColumnSeparator(separator).withHeader())
                .writeValues(stream)) {
            it.values.mapNullTo("null")
        }

    private fun writerWithoutHeader(rec: Record) =
        RecordWriter<Record, Iterable<Any>>(csvMapper
                .writerFor(Iterable::class.java)
                // See https://github.com/FasterXML/jackson-dataformats-text/issues/10
                // withNullValue() does not apply to arrays and collections
                .with(rec.toFormatSchema().withColumnSeparator(separator))
                .writeValues(stream)) {
            it.values.mapNullTo("null")
        }

    private fun Record.toFormatSchema() =
        CsvSchema.Builder().also { builder ->
            forEach { k, _ -> builder.addColumn(k) }
        }.build()

    private class RecordWriter<T, U>(
        val writer: SequenceWriter,
        val mapper: (T) -> U
    ) : Closeable {

        fun write(value: T) {
            writer.write(mapper(value))
        }

        override fun close() {
            writer.close()
        }
    }
}
