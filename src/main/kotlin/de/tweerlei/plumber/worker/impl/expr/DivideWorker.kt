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
package de.tweerlei.plumber.worker.impl.expr

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.types.DoubleValue
import de.tweerlei.plumber.worker.types.Value

class DivideWorker(
    private val value: WorkItemAccessor<Value>,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.get().let { left ->
            value(item).let { right ->
                when {
                    left is DoubleValue || right is DoubleValue -> (left.toNumber().toDouble() / right.toNumber().toDouble()).safeTruncate()
                    else -> when (val divisor = right.toNumber().toLong()) {
                        0L -> left.toNumber().toDouble() / 0.0
                        else -> left.toNumber().toLong() / divisor
                    }
                }
            }
        }.also {
            item.set(it)
        }.let { true }
}
