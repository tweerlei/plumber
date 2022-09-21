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
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import java.io.File
import java.time.Instant

class FileListWorker(
    private val dir: String,
    private val recursive: Boolean,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        File(dir.ifEmpty { "." })
            .let { directory ->
                directory.listRecursively()
                .all { file ->
                    fn(file.toWorkItem(directory))
                }
            }
    }

    private fun File.listRecursively(): Sequence<File> =
        when {
            isFile -> sequenceOf(this)
            else -> listFiles()
                .orEmpty()
                .apply { sort() }
                .asSequence()
                .let { files ->
                    when (recursive) {
                        true -> files.flatMap { it.listRecursively() }
                        false -> files.filter { it.isFile }
                    }
                }
        }

    private fun File.toWorkItem(directory: File) =
        relativeTo(directory).path.let { relativePath ->
            WorkItem.from(
                relativePath,
                WellKnownKeys.PATH to directory.absolutePath,
                WellKnownKeys.NAME to relativePath,
                WellKnownKeys.SIZE to length(),
                WellKnownKeys.LAST_MODIFIED to Instant.ofEpochMilli(lastModified())
            )
        }
}
