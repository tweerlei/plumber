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
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom

class DynamoDBPutWorker(
    private val tableName: String,
    private val amazonDynamoDBClient: AmazonDynamoDB,
    private val objectMapper: ObjectMapper,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirstAs<Record>(WellKnownKeys.RECORD)
            .toDynamoDB(objectMapper)
            .also { attributes ->
                putItem(
                    tableName.ifEmptyGetFrom(item, DynamoDBKeys.TABLE_NAME),
                    attributes
                )
            }.let { true }

    private fun putItem(table: String, item: Map<String, AttributeValue>) =
        PutItemRequest(table, item)
            .let { request -> amazonDynamoDBClient.putItem(request) }
}
