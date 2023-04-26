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
package de.tweerlei.plumber.worker.impl.aggregate

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker

class LastWorker(
    worker: Worker
): AggregateWorker<WorkItem?>(worker) {

    override fun createAggregate(key: String) =
        null

    override fun updateGroupState(item: WorkItem, key: String, aggregate: WorkItem?) =
        item

    override fun shouldPassOn(item: WorkItem, key: String, aggregate: WorkItem?): Boolean =
        false

    override fun groupStateOnClose(key: String, aggregate: WorkItem?) {
        if (aggregate != null)
            passOn(aggregate)
    }
}
