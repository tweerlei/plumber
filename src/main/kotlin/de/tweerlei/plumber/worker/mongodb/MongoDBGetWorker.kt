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
package de.tweerlei.plumber.worker.mongodb

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.MongoClient
import de.tweerlei.plumber.worker.*
import org.bson.Document

class MongoDBGetWorker(
    private val databaseName: String,
    private val collectionName: String,
    private val primaryKey: String,
    private val mongoClient: MongoClient,
    private val objectMapper: ObjectMapper,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirstAs<JsonNode>(WellKnownKeys.NODE)
            .toMongoDB(objectMapper)
            .let { attributes ->
                fetchDocument(
                    item.getIfEmpty(databaseName, MongoDBKeys.DATABASE_NAME),
                    item.getIfEmpty(collectionName, MongoDBKeys.COLLECTION_NAME),
                    attributes.extractKey(primaryKey)
                )
            }.fromMongoDB(objectMapper)
            .also { node ->
                item.set(node)
                item.set(node, WellKnownKeys.NODE)
                item.set(databaseName, MongoDBKeys.DATABASE_NAME)
                item.set(collectionName, MongoDBKeys.COLLECTION_NAME)
            }.let { true }

    private fun fetchDocument(database: String, collection: String, item: Document) =
        mongoClient.getDatabase(database).getCollection(collection).find(item).single()
}
