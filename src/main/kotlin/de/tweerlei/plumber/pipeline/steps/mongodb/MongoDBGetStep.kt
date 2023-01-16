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
package de.tweerlei.plumber.pipeline.steps.mongodb

import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.mongodb.MongoClientFactory
import de.tweerlei.plumber.worker.impl.mongodb.MongoDBGetWorker
import de.tweerlei.plumber.worker.impl.mongodb.MongoDBKeys
import org.springframework.stereotype.Service

@Service("mongodb-readWorker")
class MongoDBGetStep(
    private val mongoClientFactory: MongoClientFactory,
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "MongoDB"
    override val name = "Fetch MongoDB document"
    override val description = "Read a document from the given MongoDB collection"
    override val help = """
        The item key will be derived from the current node.
        If the argument is omitted, the database and collection names will be taken from a previously read MongoDB item.
        The current value will be set to the read JSON node, which will also be available to node-* steps. 
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.primaryKey.name} specifies the primary key property.
    """.trimIndent()
    override val example = """
        value:123
        node-set:_id
        mongodb-get:myTable
    """.trimIndent()
    override var argDescription = "<collection>"

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.NODE,
        MongoDBKeys.DATABASE_NAME,
        MongoDBKeys.COLLECTION_NAME
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        mongoClientFactory.createClient()
            .let { client ->
                MongoDBGetWorker(
                    mongoClientFactory.getDefaultDatabase(),
                    arg,
                    params.primaryKey.toMongoDBPrimaryKey(),
                    client,
                    objectMapper,
                    w
                )
            }
}
