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
package de.tweerlei.plumber.pipeline.steps.dynamodb

import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBClientFactory
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBDeleteWorker
import org.springframework.stereotype.Service

@Service("dynamodb-deleteWorker")
class DynamoDBDeleteStep(
    private val dynamoDBClientFactory: DynamoDBClientFactory
): ProcessingStep {

    override val group = "AWS DynamoDB"
    override val name = "Delete DynamoDB item"
    override val description = "Delete an element from the given DynamoDB table"
    override fun argDescription() = "<table>"

    override fun expectedInputFor(arg: String) = Record::class.java
    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.RECORD
    )

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        dynamoDBClientFactory.createAmazonDynamoDBClient(parallelDegree, params.assumeRoleArn)
            .let { client ->
                DynamoDBDeleteWorker(
                    arg,
                    params.partitionKey.ifEmpty { throw IllegalArgumentException("No partition key specified") },
                    params.rangeKey,
                    client,
                    w
                )
            }
}
