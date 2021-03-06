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
package de.tweerlei.plumber.worker.file

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import java.io.*

class LineWriteWorker(
    private val outputFile: File,
    private val separator: ByteArray,
    worker: Worker
): DelegatingWorker(worker) {

    private lateinit var stream: OutputStream

    override fun onOpen() {
        stream = FileOutputStream(outputFile)
    }

    override fun doProcess(item: WorkItem) =
        stream.write(item.getByteArray())
            .also { stream.write(separator) }
            .let { true }

    override fun onClose() {
        stream.close()
    }
}
