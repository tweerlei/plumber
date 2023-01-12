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
import de.tweerlei.plumber.worker.types.ByteArrayValue
import de.tweerlei.plumber.worker.types.InstantValue
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.StringValue
import java.util.zip.ZipInputStream

class ZipReadWorker(
    private val inputStreamProvider: InputStreamProvider,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        ZipInputStream(inputStreamProvider.open()).use { stream ->
            var keepGenerating = true
            while (keepGenerating) {
                keepGenerating = stream.nextEntry
                    ?.let { entry ->
                        stream.readAllBytes().let { bytes ->
                            fn(WorkItem.of(
                                ByteArrayValue.of(bytes),
                                WellKnownKeys.NAME to StringValue.of(entry.name),
                                WellKnownKeys.SIZE to LongValue.of(entry.size),
                                WellKnownKeys.LAST_MODIFIED to InstantValue.ofEpochMilli(entry.time)
                            ))
                        }
                    } ?: false
            }
        }
    }
}
