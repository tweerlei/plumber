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
import java.io.IOException

class FileDeleteWorker(
    private val dir: File,
    worker: Worker
): DelegatingWorker(worker) {

    override fun onOpen() {
        dir.mkdirs()
    }

    override fun doProcess(item: WorkItem) =
        File(dir, item.getString(WellKnownKeys.NAME))
            .let { file ->
                file.delete() || throw IOException("Could not delete $file")
            }
}
