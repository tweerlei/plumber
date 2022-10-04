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

import de.tweerlei.plumber.worker.InputStreamProvider
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.StringValue
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class LineReadWorker(
    private val inputStreamProvider: InputStreamProvider,
    private val charset: Charset,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    private lateinit var reader: BufferedReader

    override fun onOpen() {
        reader = BufferedReader(InputStreamReader(inputStreamProvider.open(), charset))
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val fileName = StringValue.of(inputStreamProvider.getName())
        var keepGenerating = true
        while (keepGenerating) {
            keepGenerating = reader.nextWorkItem(fileName)
                ?.let { workItem -> fn(workItem) }
                ?: false
        }
    }

    private fun BufferedReader.nextWorkItem(fileName: StringValue) =
        readLine()
            ?.let { line ->
                WorkItem.of(
                    StringValue.of(line),
                    WellKnownKeys.NAME to fileName
                )
            }

    override fun onClose() {
        reader.close()
    }
}
