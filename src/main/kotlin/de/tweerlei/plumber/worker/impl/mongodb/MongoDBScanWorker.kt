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
package de.tweerlei.plumber.worker.impl.mongodb

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mongodb.client.MongoClient
import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.types.Range
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.coerceToJsonNode
import mu.KLogging
import org.bson.Document
import java.util.*

class MongoDBScanWorker(
    private val databaseName: String,
    private val collectionName: String,
    private val primaryKey: String,
    private val selectFields: Set<String>,
    private val numberOfFilesPerRequest: Int,
    private val mongoClient: MongoClient,
    private val objectMapper: ObjectMapper,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object : KLogging()

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val range = item.getOptionalAs<Range>(WellKnownKeys.RANGE)
        val startAfter = range?.startAfter
        val endWith = range?.endWith
        logger.info { "fetching elements from $startAfter to $endWith" }

        var firstKey: Any? = null
        var lastKey: Any? = null
        var itemCount = 0
        listDocuments(startAfter, endWith)
            .all { resultItem ->
                resultItem.fromMongoDB(objectMapper).let { row ->
                    if (fn(row.toWorkItem())) {
                        itemCount++
                        if (firstKey == null) firstKey = row[primaryKey]
                        lastKey = row[primaryKey]
                        true
                    } else {
                        false
                    }
                }
            }

        logger.info { "fetched $itemCount documents from $startAfter to $endWith, first key: $firstKey, last key: $lastKey" }
    }

    private fun listDocuments(startAfter: Any?, endWith: Any?) =
        filterFor(startAfter, endWith).let { filter ->
            mongoClient.getDatabase(databaseName).getCollection(collectionName)
                .find(filter)
                .batchSize(numberOfFilesPerRequest)
                .projection(fieldsToSelect())
        }

    private fun filterFor(startAfter: Any?, endWith: Any?) =
        when {
            startAfter != null && endWith != null -> JsonNodeFactory.instance.objectNode().apply {
                set<ObjectNode>(primaryKey, JsonNodeFactory.instance.objectNode().apply {
                    set<ObjectNode>("\$gt", startAfter.coerceToJsonNode())
                    set<ObjectNode>("\$lte", endWith.coerceToJsonNode())
                })
            }
            startAfter != null -> JsonNodeFactory.instance.objectNode().apply {
                set<ObjectNode>(primaryKey, JsonNodeFactory.instance.objectNode().apply {
                    set<ObjectNode>("\$gt", startAfter.coerceToJsonNode())
                })
            }
            endWith != null -> JsonNodeFactory.instance.objectNode().apply {
                set<ObjectNode>(primaryKey, JsonNodeFactory.instance.objectNode().apply {
                    set<ObjectNode>("\$lte", endWith.coerceToJsonNode())
                })
            }
            else -> JsonNodeFactory.instance.objectNode()
        }.toMongoDB(objectMapper)

    private fun fieldsToSelect() =
        selectFields.ifEmpty { null }?.fold(Document()) { doc, field ->
            doc.apply { this[field] = 1 }
        }

    private fun JsonNode.toWorkItem() =
        WorkItem.of(
            this,
            WellKnownKeys.NODE to this,
            MongoDBKeys.DATABASE_NAME to databaseName,
            MongoDBKeys.COLLECTION_NAME to collectionName,
            MongoDBKeys.PRIMARY_KEY to this[primaryKey],
        )
}
