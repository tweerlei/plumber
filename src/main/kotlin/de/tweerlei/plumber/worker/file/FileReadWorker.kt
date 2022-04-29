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

import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import java.io.File
import java.io.FileInputStream

class FileReadWorker(
    private val dir: String,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirstString(WellKnownKeys.NAME)
            .let { name ->
                File(item.getIfEmpty(dir, FileKeys.FILE_PATH).ifEmpty { "." })
                    .let { directory ->
                        File(directory, name)
                            .let { file ->
                                FileInputStream(file).use { stream ->
                                    stream.readAllBytes()
                                }
                            }.also { bytes ->
                                item.set(directory.absolutePath, FileKeys.FILE_PATH)
                                item.set(name, FileKeys.FILE_NAME)
                                item.set(bytes)
                            }
                    }
            }.let { true }
}
