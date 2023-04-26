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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mongodb.client.MongoClient
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.*
import mu.KLogging
import org.bson.Document

class MongoDBScanWorker(
    private val databaseName: String,
    private val collectionName: WorkItemAccessor<String>,
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
        val actualDatabaseName = StringValue.of(databaseName)
        val actualCollectionName = StringValue.of(collectionName(item))
        val range = (item.getOptional(WellKnownKeys.RANGE) ?: Range()).toRange()
        logger.info { "fetching elements from ${range.startAfter} to ${range.endWith}" }

        var firstKey: Any? = null
        var lastKey: Any? = null
        var itemCount = 0
        listDocuments(actualCollectionName.toAny(), range.startAfter, range.endWith)
            .all { resultItem ->
                resultItem.fromMongoDB(objectMapper).let { row ->
                    if (fn(Node(row).toWorkItem(actualDatabaseName, actualCollectionName))) {
                        itemCount++
                        if (firstKey == null) firstKey = row[primaryKey]
                        lastKey = row[primaryKey]
                        true
                    } else {
                        false
                    }
                }
            }

        logger.info { "fetched $itemCount documents from ${range.startAfter} to ${range.endWith}, first key: $firstKey, last key: $lastKey" }
    }

    private fun listDocuments(collection: String, startAfter: ComparableValue, endWith: ComparableValue) =
        filterFor(startAfter, endWith).let { filter ->
            mongoClient.getDatabase(databaseName).getCollection(collection)
                .find(filter)
                .batchSize(numberOfFilesPerRequest)
                .projection(fieldsToSelect())
        }

    private fun filterFor(startAfter: ComparableValue, endWith: ComparableValue) =
        when {
            startAfter !is NullValue && endWith !is NullValue -> JsonNodeFactory.instance.objectNode().apply {
                set<ObjectNode>(primaryKey, JsonNodeFactory.instance.objectNode().apply {
                    set<ObjectNode>("\$gt", startAfter.toJsonNode())
                    set<ObjectNode>("\$lte", endWith.toJsonNode())
                })
            }
            startAfter !is NullValue -> JsonNodeFactory.instance.objectNode().apply {
                set<ObjectNode>(primaryKey, JsonNodeFactory.instance.objectNode().apply {
                    set<ObjectNode>("\$gt", startAfter.toJsonNode())
                })
            }
            endWith !is NullValue -> JsonNodeFactory.instance.objectNode().apply {
                set<ObjectNode>(primaryKey, JsonNodeFactory.instance.objectNode().apply {
                    set<ObjectNode>("\$lte", endWith.toJsonNode())
                })
            }
            else -> JsonNodeFactory.instance.objectNode()
        }.toMongoDB(objectMapper)

    private fun fieldsToSelect() =
        selectFields.ifEmpty { null }?.fold(Document()) { doc, field ->
            doc.apply { this[field] = 1 }
        }

    private fun Node.toWorkItem(database: StringValue, collection: StringValue) =
        WorkItem.of(
            this,
            WellKnownKeys.NODE to this,
            MongoDBKeys.DATABASE_NAME to database,
            MongoDBKeys.COLLECTION_NAME to collection,
            MongoDBKeys.PRIMARY_KEY to getValue(primaryKey)
        )
}
