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

import de.tweerlei.plumber.worker.OutputStreamProvider
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import java.io.*
import java.nio.charset.Charset

class LineWriteWorker(
    private val outputStreamProvider: OutputStreamProvider,
    private val charset: Charset,
    private val separator: String,
    worker: Worker
): DelegatingWorker(worker) {

    private lateinit var writer: Writer

    override fun onOpen() {
        writer = OutputStreamWriter(outputStreamProvider.open(), charset)
    }

    override fun doProcess(item: WorkItem) =
        item.get().toString()
            .also { bytes -> writer.write(bytes) }
            .also { writer.write(separator) }
            .let { true }

    override fun onClose() {
        writer.close()
    }
}
