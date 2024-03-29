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
import com.mongodb.client.MongoClient
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import de.tweerlei.plumber.worker.types.Node
import de.tweerlei.plumber.worker.types.StringValue
import org.bson.Document

class MongoDBGetWorker(
    private val databaseName: String,
    private val collectionName: WorkItemAccessor<String>,
    private val primaryKey: String,
    private val mongoClient: MongoClient,
    private val objectMapper: ObjectMapper,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirst(WellKnownKeys.NODE)
            .toJsonNode()
            .toMongoDB(objectMapper)
            .let { attributes ->
                databaseName.ifEmptyGetFrom(item, MongoDBKeys.DATABASE_NAME)
                    .let { actualDatabaseName ->
                        collectionName(item).ifEmptyGetFrom(item, MongoDBKeys.COLLECTION_NAME)
                            .let { actualCollectionName ->
                                fetchDocument(
                                    actualDatabaseName,
                                    actualCollectionName,
                                    attributes.extractKey(primaryKey)
                                )
                                    .fromMongoDB(objectMapper)
                                    .let { node -> Node(node) }
                                    .also { node ->
                                        item.set(node)
                                        item.set(node, WellKnownKeys.NODE)
                                        item.set(StringValue.of(actualDatabaseName), MongoDBKeys.DATABASE_NAME)
                                        item.set(StringValue.of(actualCollectionName), MongoDBKeys.COLLECTION_NAME)
                                    }
                            }
                    }
            }.let { true }

    private fun fetchDocument(database: String, collection: String, item: Document) =
        mongoClient.getDatabase(database).getCollection(collection).find(item).single()
}
