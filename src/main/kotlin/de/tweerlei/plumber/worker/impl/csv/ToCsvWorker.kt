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
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.Record

class ToCsvWorker(
    csvMapper: CsvMapper,
    separator: Char,
    worker: Worker
): DelegatingWorker(worker) {

    companion object {
        const val CONTENT_TYPE_CSV = "text/csv"
    }

    private val writer = csvMapper
        .writerFor(Iterable::class.java)
        // See https://github.com/FasterXML/jackson-dataformats-text/issues/10
        // withNullValue() does not apply to arrays and collections
        .with(CsvSchema.emptySchema().withColumnSeparator(separator))

    override fun doProcess(item: WorkItem) =
        item.getFirstAs<Record>(WellKnownKeys.RECORD)
            .let { obj ->
                writer.writeValueAsString(obj.values.mapNullTo("null"))
                    .also { str ->
                        item.set(str)
                        item.set(WellKnownKeys.CONTENT_TYPE, CONTENT_TYPE_CSV)
                    }
            }.let { true }
}
