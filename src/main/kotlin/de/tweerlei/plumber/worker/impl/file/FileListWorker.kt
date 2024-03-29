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
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.InstantValue
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.StringValue
import java.io.File

class FileListWorker(
    private val dir: WorkItemAccessor<String>,
    private val recursive: Boolean,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        File(dir(item).ifEmpty { "." })
            .let { directory ->
                val path = StringValue.of(directory.absolutePath)
                directory.listRecursively()
                    .all { file ->
                        fn(file.toWorkItem(directory, path))
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

    private fun File.toWorkItem(directory: File, path: StringValue) =
        relativeTo(directory).path
            .let { StringValue.of(it) }
            .let { relativePath ->
                WorkItem.of(
                    relativePath,
                    WellKnownKeys.PATH to path,
                    WellKnownKeys.NAME to relativePath,
                    WellKnownKeys.SIZE to LongValue.of(length()),
                    WellKnownKeys.LAST_MODIFIED to InstantValue.ofEpochMilli(lastModified())
                )
            }
}
