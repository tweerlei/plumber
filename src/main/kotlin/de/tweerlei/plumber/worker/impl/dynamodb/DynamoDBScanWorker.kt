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
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.ScanResult
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.*
import mu.KLogging

class DynamoDBScanWorker(
    private val tableName: WorkItemAccessor<String>,
    private val partitionKey: String,
    private val rangeKey: String,
    private val selectFields: Set<String>,
    private val numberOfFilesPerRequest: Int,
    private val amazonDynamoDBClient: AmazonDynamoDB,
    private val objectMapper: ObjectMapper,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object : KLogging()

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val actualTableName = StringValue.of(tableName(item))
        val primaryRange = (item.getOptional(WellKnownKeys.RANGE) ?: Range()).toRange()
        val secondaryRange = (item.getOptional(WellKnownKeys.SECONDARY_RANGE) ?: Range()).toRange()
        val startAfter = keyFrom(primaryRange.startAfter, secondaryRange.startAfter)
        val endWith = keyFrom(primaryRange.endWith, secondaryRange.endWith)
        logger.info { "fetching elements from $startAfter to $endWith" }

        var result: ScanResult? = null
        var firstKey: Value? = null
        var lastKey: Value? = null
        var itemCount = 0
        do {
            result = listFilenames(actualTableName.toAny(), startAfter?.toDynamoDB(objectMapper), result?.lastEvaluatedKey)
            logger.debug { "fetched ${result.items.size} items" }
            result.items.forEach { resultItem ->
                resultItem.fromDynamoDB(objectMapper).let { row ->
                    if (row.isNotAfter(endWith)) {
                        if (fn(row.toWorkItem(actualTableName))) {
                            itemCount++
                            if (firstKey == null) firstKey = row.getValue(partitionKey)
                            lastKey = row.getValue(partitionKey)
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

    private fun listFilenames(table: String, startAfter: Map<String, AttributeValue>?, continueAfter: Map<String, AttributeValue>?) =
        ScanRequest()
            .withTableName(table)
            .withLimit(numberOfFilesPerRequest)
            .withExclusiveStartKey(continueAfter ?: startAfter)
            .withProjectionExpression(selectFields.ifEmpty { null }?.joinToString(","))
            .let { request -> amazonDynamoDBClient.scan(request) }

    private fun keyFrom(partitionKeyValue: ComparableValue, rangeKeyValue: ComparableValue) =
        when {
            partitionKeyValue is NullValue -> null
            rangeKey.isEmpty() || rangeKeyValue is NullValue ->
                Record.of(
                    partitionKey to partitionKeyValue
                )
            else ->
                Record.of(
                    partitionKey to partitionKeyValue,
                    rangeKey to rangeKeyValue
                )
        }

    private fun Record.isNotAfter(maxKey: Record?) =
        when {
            maxKey == null -> true
            this.toAny()[partitionKey] as ComparableValue > maxKey.toAny()[partitionKey] as ComparableValue -> false
            rangeKey.isEmpty() || maxKey.toAny()[rangeKey] == null -> true
            this.toAny()[rangeKey] as ComparableValue > maxKey.toAny()[rangeKey] as ComparableValue -> false
            else -> true
        }

    private fun Record.toWorkItem(actualTableName: StringValue) =
        WorkItem.of(
            this,
            WellKnownKeys.RECORD to this,
            DynamoDBKeys.TABLE_NAME to actualTableName,
            DynamoDBKeys.PARTITION_KEY to getValue(partitionKey),
            DynamoDBKeys.RANGE_KEY to if (rangeKey.isEmpty()) NullValue.INSTANCE else getValue(rangeKey)
        )
}
