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
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.ScanResult
import de.tweerlei.plumber.worker.*
import mu.KLogging

class DynamoDBScanWorker(
    private val tableName: String,
    private val partitionKey: String,
    private val rangeKey: String?,
    private val numberOfFilesPerRequest: Int,
    private val amazonDynamoDBClient: AmazonDynamoDB,
    limit: Int,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object : KLogging()

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val startAfter = when (item.containsKey(WellKnownKeys.START_AFTER_KEY)) {
            true -> mapOf(partitionKey to AttributeValue(item.getString(WellKnownKeys.START_AFTER_KEY)))
            else -> null
        }
        val endWith = when (item.containsKey(WellKnownKeys.END_WITH_KEY)) {
            true -> item.getString(WellKnownKeys.END_WITH_KEY)
            else -> null
        }
        logger.info("fetching elements from $startAfter to $endWith")

        var result: ScanResult? = null
        var firstKey: String? = null
        var lastKey: String? = null
        var itemCount = 0
        do {
            result = listFilenames(startAfter, result?.lastEvaluatedKey)
            result.items.forEach { row ->
                val key = row[partitionKey]?.s.orEmpty()
                if (endWith == null || key <= endWith) {
                    if (fn(row.fromDynamoDB().toWorkItem())) {
                        itemCount++
                        if (firstKey == null) firstKey = key
                        lastKey = key
                    } else {
                        result.lastEvaluatedKey = null
                    }
                } else {
                    result.lastEvaluatedKey = null
                }
            }
        } while (result?.lastEvaluatedKey != null)

        logger.info("fetched $itemCount filenames from $startAfter to $endWith, first key: $firstKey, last key: $lastKey")
    }

    private fun listFilenames(startAfter: Map<String, AttributeValue>?, continueAfter: Map<String, AttributeValue>?) =
        ScanRequest()
            .withTableName(tableName)
            .withLimit(numberOfFilesPerRequest)
            .withExclusiveStartKey(continueAfter ?: startAfter)
            .let { request -> amazonDynamoDBClient.scan(request) }

    private fun Record.toWorkItem() =
        WorkItem.of(
            this,
            WellKnownKeys.RECORD to this,
            DynamoDBKeys.TABLE_NAME to tableName,
            DynamoDBKeys.PARTITION_KEY to this[partitionKey],
            DynamoDBKeys.RANGE_KEY to rangeKey?.let { this[rangeKey] }
        )
}
