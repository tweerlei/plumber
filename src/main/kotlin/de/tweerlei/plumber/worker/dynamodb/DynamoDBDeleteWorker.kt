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
package de.tweerlei.plumber.worker.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest
import de.tweerlei.plumber.worker.*

class DynamoDBDeleteWorker(
    private val tableName: String,
    private val partitionKey: String,
    private val rangeKey: String?,
    private val amazonDynamoDBClient: AmazonDynamoDB,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirstAs<Record>(WellKnownKeys.RECORD)
            .toDynamoDB()
            .let { attributes ->
                deleteItem(
                    tableName.ifEmptyGetFrom(item, DynamoDBKeys.TABLE_NAME),
                    attributes.extractKey(partitionKey, rangeKey)
                )
            }.let { true }

    private fun deleteItem(table: String, item: Map<String, AttributeValue>) =
        DeleteItemRequest(table, item)
            .let { request -> amazonDynamoDBClient.deleteItem(request) }
}
