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
import de.tweerlei.plumber.worker.types.coerceToByteArray
import de.tweerlei.plumber.worker.types.coerceToString
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

class FileWriteWorker(
    private val dir: String,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getOptional(WellKnownKeys.NAME).coerceToString()
            .let { name ->
                File(dir.ifEmptyGetFrom(item, WellKnownKeys.PATH).ifEmpty { "." })
                    .let { directory ->
                        directory.mkdirs()
                        File(directory, name)
                            .let { file ->
                                FileOutputStream(file).use { stream ->
                                    stream.write(item.getOptional().coerceToByteArray()
                                    )
                                }
                                item.getOptionalAs<Instant>(WellKnownKeys.LAST_MODIFIED)?.let { lastMod ->
                                    file.setLastModified(lastMod.toEpochMilli())
                                }
                           }
                    }
            }.let { true }
}