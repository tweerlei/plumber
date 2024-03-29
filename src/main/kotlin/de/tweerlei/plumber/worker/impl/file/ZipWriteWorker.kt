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
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipWriteWorker(
    private val outputStreamProvider: OutputStreamProvider,
    worker: Worker
): DelegatingWorker(worker) {

    private lateinit var stream: ZipOutputStream

    override fun onOpen() {
        stream = ZipOutputStream(outputStreamProvider.open())
    }

    override fun doProcess(item: WorkItem) =
        item.get(WellKnownKeys.NAME).toString()
            .let { name ->
                ZipEntry(name).apply {
                    item.getOptional(WellKnownKeys.LAST_MODIFIED)?.let { lastMod ->
                        time = lastMod.toLong()
                    }
                }.let { entry ->
                    stream.putNextEntry(entry)
                    stream.write(item.get().toByteArray())
                    stream.closeEntry()
                }
            }.let { true }

    override fun onClose() {
        stream.close()
    }
}
