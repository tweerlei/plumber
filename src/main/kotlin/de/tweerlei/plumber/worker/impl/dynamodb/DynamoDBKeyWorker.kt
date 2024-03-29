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
package de.tweerlei.plumber.worker.impl.dynamodb

import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.NullValue
import de.tweerlei.plumber.worker.types.Value
import de.tweerlei.plumber.worker.types.toComparableValue

class DynamoDBKeyWorker(
    private val partitionKey: String,
    private val rangeKey: String,
    private val rangeKeyValue: WorkItemAccessor<Value>,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.get().toString()
            .let { key ->
                when {
                    rangeKey.isEmpty() -> {
                        item.set(key.toComparableValue(), DynamoDBKeys.PARTITION_KEY)
                        item.set(NullValue.INSTANCE, DynamoDBKeys.RANGE_KEY)
                        Record.of(
                            partitionKey to item.get(DynamoDBKeys.PARTITION_KEY)
                        )
                    }
                    else -> {
                        item.set(key.toComparableValue(), DynamoDBKeys.PARTITION_KEY)
                        item.set(rangeKeyValue(item), DynamoDBKeys.RANGE_KEY)
                        Record.of(
                            partitionKey to item.get(DynamoDBKeys.PARTITION_KEY),
                            rangeKey to item.get(DynamoDBKeys.RANGE_KEY)
                        )
                    }
                }
            }.let { record ->
                item.set(record, WellKnownKeys.RECORD)
            }.let { true }
}
