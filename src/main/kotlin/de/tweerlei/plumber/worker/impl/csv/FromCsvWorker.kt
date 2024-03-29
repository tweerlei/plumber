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
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.Record

class FromCsvWorker(
    csvMapper: CsvMapper,
    separator: Char,
    worker: Worker
): DelegatingWorker(worker) {

    private val reader = csvMapper
        .readerFor(Array<String>::class.java)
        .with(CsvSchema.emptySchema().withColumnSeparator(separator))

    override fun doProcess(item: WorkItem) =
        item.get().toString()
            .let { value ->
                reader.readValue<Array<String>>(value)
                    ?.let { arr ->
                        Record.ofComparableValues(arr)
                    }?.also { obj ->
                        item.set(obj)
                        item.set(obj, WellKnownKeys.RECORD)
                    }
            }?.let { true }
            ?: false
}
