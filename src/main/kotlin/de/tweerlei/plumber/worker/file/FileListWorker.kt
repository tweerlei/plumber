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
import de.tweerlei.plumber.worker.GeneratingWorker
import de.tweerlei.plumber.worker.Worker
import java.io.File
import java.time.Instant

class FileListWorker(
    private val dir: File,
    limit: Int,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        dir.listFiles { file -> file.isFile }
            .orEmpty()
            .all { file ->
                fn(file.toWorkItem())
            }
        }

    private fun File.toWorkItem() =
        WorkItem.of(
            name,
            WellKnownKeys.NAME to name,
            WellKnownKeys.SIZE to length(),
            WellKnownKeys.LAST_MODIFIED to Instant.ofEpochMilli(lastModified()),
            FileKeys.FILE_PATH to dir.absolutePath,
            FileKeys.FILE_NAME to name
        )
}
