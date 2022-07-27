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

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import de.tweerlei.plumber.worker.types.coerceToString
import java.io.File
import java.io.FileInputStream
import java.time.Instant

class FileReadWorker(
    private val dir: String,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirst(WellKnownKeys.NAME).coerceToString()
            .let { name ->
                File(dir.ifEmptyGetFrom(item, WellKnownKeys.PATH).ifEmpty { "." })
                    .let { directory ->
                        File(directory, name)
                            .let { file ->
                                FileInputStream(file).use { stream ->
                                    stream.readAllBytes()
                                }.also { bytes ->
                                    item.set(directory.absolutePath, WellKnownKeys.PATH)
                                    item.set(name, WellKnownKeys.NAME)
                                    item.set(file.length(), WellKnownKeys.SIZE)
                                    item.set(Instant.ofEpochMilli(file.lastModified()), WellKnownKeys.LAST_MODIFIED)
                                    item.set(bytes)
                                }
                            }
                    }
            }.let { true }
}
