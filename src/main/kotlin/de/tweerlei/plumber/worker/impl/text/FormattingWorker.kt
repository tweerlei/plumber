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
package de.tweerlei.plumber.worker.impl.text

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.Worker

class FormattingWorker(
    private val formatString: String,
    worker: Worker
): DelegatingWorker(worker) {

    companion object {
        private val REGEX = Regex("\\$\\{([^}]*)\\}")
    }

    override fun doProcess(item: WorkItem) =
        REGEX.replace(formatString) { match ->
            match.groups[1]?.value
                ?.let { varName ->
                    item.getOptional(varName)?.toString()
                }.orEmpty()
        }.also { result ->
            item.set(result)
        }.let { true }
}