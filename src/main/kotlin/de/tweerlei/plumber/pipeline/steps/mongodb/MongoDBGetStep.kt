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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.pipeline.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.mongodb.MongoClientFactory
import de.tweerlei.plumber.worker.mongodb.MongoDBGetWorker
import de.tweerlei.plumber.worker.mongodb.MongoDBKeys
import org.springframework.stereotype.Service

@Service("mongodb-readWorker")
class MongoDBGetStep(
    private val mongoClientFactory: MongoClientFactory,
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "MongoDB"
    override val name = "Fetch MongoDB document"
    override val description = "Read a document from the given MongoDB collection"

    override fun expectedInputFor(arg: String) = JsonNode::class.java
    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.NODE,
        MongoDBKeys.DATABASE_NAME,
        MongoDBKeys.COLLECTION_NAME
    )

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
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
                    params.primaryKey.ifEmpty { "_id" },
                    client,
                    objectMapper,
                    w
                )
            }
}
