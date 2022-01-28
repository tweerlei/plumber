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
    private val startAfterRange: String?,
    private val endWithRange: String?,
    private val numberOfFilesPerRequest: Int,
    private val amazonDynamoDBClient: AmazonDynamoDB,
    limit: Int,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object : KLogging()

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val startAfter = when (item.has(WellKnownKeys.START_AFTER_KEY)) {
            true -> Record(
                partitionKey to item.getOptionalAs<Comparable<Any>>(WellKnownKeys.START_AFTER_KEY)
            ).also { map ->
                if (rangeKey != null && startAfterRange != null)
                    map[rangeKey] = convertType(startAfterRange)
            }
            else -> null
        }
        val endWith = when (item.has(WellKnownKeys.END_WITH_KEY)) {
            true -> Record(
                partitionKey to item.getOptionalAs<Comparable<Any>>(WellKnownKeys.END_WITH_KEY)
            ).also { map ->
                if (rangeKey != null && endWithRange != null)
                    map[rangeKey] = convertType(endWithRange)
            }
            else -> null
        }
        logger.info { "fetching elements from $startAfter to $endWith" }

        var result: ScanResult? = null
        var firstKey: Any? = null
        var lastKey: Any? = null
        var itemCount = 0
        do {
            result = listFilenames(startAfter?.toDynamoDB(), result?.lastEvaluatedKey)
            logger.debug { "fetched ${result.items.size} items" }
            result.items.forEach { resultItem ->
                resultItem.fromDynamoDB().let { row ->
                    if (isKeyBefore(row, endWith)) {
                        if (fn(row.toWorkItem())) {
                            itemCount++
                            if (firstKey == null) firstKey = row[partitionKey]
                            lastKey = row[partitionKey]
                        } else {
                            result.lastEvaluatedKey = null
                        }
                    } else {
                        result.lastEvaluatedKey = null
                    }
                }
            }
        } while (result?.lastEvaluatedKey != null)

        logger.info { "fetched $itemCount filenames from $startAfter to $endWith, first key: $firstKey, last key: $lastKey" }
    }

    private fun convertType(value: String) =
        WorkItem.of(null).let { item ->
            item.setString(value)
            item.getAs<Comparable<Any>>()
        }

    @Suppress("UNCHECKED_CAST")
    private fun isKeyBefore(row: Record, maxKey: Record?) =
        when {
            maxKey == null -> true
            row[partitionKey] as Comparable<Any> > maxKey[partitionKey] as Comparable<Any> -> false
            rangeKey == null || maxKey[rangeKey] == null -> true
            row[rangeKey] as Comparable<Any> > maxKey[rangeKey] as Comparable<Any> -> false
            else -> true
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
