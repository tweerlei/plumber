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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import de.tweerlei.plumber.worker.types.StringValue

class DynamoDBGetWorker(
    private val tableName: WorkItemAccessor<String>,
    private val partitionKey: String,
    private val rangeKey: String?,
    private val amazonDynamoDBClient: AmazonDynamoDB,
    private val objectMapper: ObjectMapper,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirst(WellKnownKeys.RECORD)
            .toRecord()
            .toDynamoDB(objectMapper)
            .let { attributes ->
                tableName(item).ifEmptyGetFrom(item, DynamoDBKeys.TABLE_NAME)
                    .let { StringValue.of(it) }
                    .let { actualTableName ->
                        getItem(
                            actualTableName.toAny(),
                            attributes.extractKey(partitionKey, rangeKey)
                        )
                            .fromDynamoDB(objectMapper)
                            .also { record ->
                                item.set(record)
                                item.set(record, WellKnownKeys.RECORD)
                                item.set(actualTableName, DynamoDBKeys.TABLE_NAME)
                            }
                    }
            }.let { true }

    private fun getItem(table: String, item: Map<String, AttributeValue>): Map<String, AttributeValue> =
        GetItemRequest(table, item)
            .let { request -> amazonDynamoDBClient.getItem(request) }
            .item
}
