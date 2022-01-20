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
import de.tweerlei.plumber.worker.*

class FromCsvWorker(
    private val csvMapper: CsvMapper,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getString()
            .let { value ->
                csvMapper.readValue(value, Array<String>::class.java)
                    ?.let { arr ->
                        arr.foldIndexed(Record()) { index, acc, value ->
                            acc[index.toString()] = value
                            acc
                        }
                    }?.also { obj ->
                        item.set(obj)
                        item.set(obj, WellKnownKeys.RECORD)
                    }
            }?.let { true }
            ?: false
}
