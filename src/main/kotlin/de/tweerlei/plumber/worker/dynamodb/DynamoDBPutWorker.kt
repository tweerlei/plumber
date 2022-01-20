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
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import de.tweerlei.plumber.worker.*

class DynamoDBPutWorker(
    private val tableName: String,
    private val amazonDynamoDBClient: AmazonDynamoDB,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getAs<Record>()
            .toDynamoDB()
            .also { attributes ->
                putItem(
                    getTableName(item),
                    attributes
                )
            }.let { true }

    private fun getTableName(item: WorkItem) =
        tableName.ifEmpty { item.getString(DynamoDBKeys.TABLE_NAME) }

    private fun putItem(table: String, item: Map<String, AttributeValue>) =
        PutItemRequest(table, item)
            .let { request -> amazonDynamoDBClient.putItem(request) }
}
