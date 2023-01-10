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
package de.tweerlei.plumber.worker.impl.attribute

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.types.*

class ConvertingWorker(
    private val type: String,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.get()
            .let { value ->
                when (type) {
                    StringValue.NAME -> value.toString()
                    LongValue.NAME -> value.toLong()
                    DoubleValue.NAME -> value.toDouble()
                    BooleanValue.NAME -> value.toBoolean()
                    InstantValue.NAME -> value.toInstant()
                    DurationValue.NAME -> value.toDuration()
                    BigIntegerValue.NAME -> value.toBigInteger()
                    BigDecimalValue.NAME -> value.toBigDecimal()
                    ByteArrayValue.NAME -> value.toByteArray()
                    Record.NAME -> value.toRecord()
                    Node.NAME -> value.toJsonNode()
                    else -> null
                }
            }?.toValue()
            ?.also { value ->
                item.set(value)
            }.let { true }
}
