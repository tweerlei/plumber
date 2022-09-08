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
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.mongodb.MongoClientFactory
import de.tweerlei.plumber.worker.impl.mongodb.MongoDBKeys
import de.tweerlei.plumber.worker.impl.mongodb.MongoDBScanWorker
import org.springframework.stereotype.Service

@Service("mongodb-listWorker")
class MongoDBScanStep(
    private val mongoClientFactory: MongoClientFactory,
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "MongoDB"
    override val name = "Scan MongoDB documents"
    override val description = "List documents from the given MongoDB table"

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
                MongoDBScanWorker(
                    mongoClientFactory.getDefaultDatabase(),
                    arg,
                    params.primaryKey.ifEmpty { "_id" },
                    params.selectFields,
                    params.numberOfFilesPerRequest,
                    client,
                    objectMapper,
                    params.maxFilesPerThread,
                    w
                )
            }
}
