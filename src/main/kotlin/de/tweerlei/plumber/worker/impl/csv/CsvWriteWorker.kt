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

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import java.io.*

class CsvWriteWorker(
    private val file: File,
    private val csvMapper: CsvMapper,
    worker: Worker
): DelegatingWorker(worker) {

    private lateinit var stream: OutputStream
    private var writer: Writer? = null

    override fun onOpen() {
        stream = FileOutputStream(file)
    }

    override fun doProcess(item: WorkItem): Boolean =
        item.getFirstAs<Record>(WellKnownKeys.RECORD)
            .let { obj ->
                writerFor(obj).writeValue(obj)
            }.let { true }

    private fun writerFor(rec: Record) =
        when (val w = writer) {
            null -> Writer.from(stream, rec, csvMapper)
            else -> w
        }

    override fun onClose() {
        writer?.close()
    }

    private class Writer(
        private val writer: ObjectWriter,
        private val generator: JsonGenerator
    ) {

        companion object {
            fun from(stream: OutputStream, rec: Record, csvMapper: CsvMapper) =
                csvMapper.writerFor(Record::class.java)
                    .with(rec.toFormatSchema())
                    .let { writer ->
                        Writer(
                            writer,
                            writer.createGenerator(stream)
                        )
                    }

            private fun Record.toFormatSchema() =
                CsvSchema.Builder().also { builder ->
                    forEach { k, _ -> builder.addColumn(k) }
                }.build()
        }

        fun writeValue(rec: Record) {
            writer.writeValue(generator, rec)
        }

        fun close() {
            generator.close()
        }
    }
}
