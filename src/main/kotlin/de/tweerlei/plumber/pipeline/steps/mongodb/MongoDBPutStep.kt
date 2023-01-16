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
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.mongodb.MongoClientFactory
import de.tweerlei.plumber.worker.impl.mongodb.MongoDBPutWorker
import org.springframework.stereotype.Service

@Service("mongodb-writeWorker")
class MongoDBPutStep(
    private val mongoClientFactory: MongoClientFactory,
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "MongoDB"
    override val name = "Put MongoDB document"
    override val description = "Insert a document into the given MongoDB collection"
    override val help = """
        This will write the current node, if any. Otherwise the current value will be used.
        If the argument is omitted, the table name will be taken from a previously read MongoDB item.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:123
        node-set:ItemID
        value:foo
        node-set:Name
        value:true
        node-set:InStock
        mongodb-write:myTable
    """.trimIndent()
    override val argDescription = "<collection>"

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        mongoClientFactory.createClient()
            .let { client ->
                MongoDBPutWorker(
                    mongoClientFactory.getDefaultDatabase(),
                    arg,
                    client,
                    objectMapper,
                    w
                )
            }
}
