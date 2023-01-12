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

import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBClientFactory
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBKeys
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBScanWorker
import org.springframework.stereotype.Service

@Service("dynamodb-listWorker")
class DynamoDBScanStep(
    private val dynamoDBClientFactory: DynamoDBClientFactory,
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "AWS DynamoDB"
    override val name = "Scan DynamoDB items"
    override val description = "List elements from the given DynamoDB table"
    override val help = """
        THe key range to scan can be specified by setting the current range and secondaryRange. If not set,
        the whole table will be listed.
        The current value will be set to the read record, which will also be available to record-* steps.
        Values in this record will be JSON nodes to support complex data types.
        Use --${AllPipelineOptions.INSTANCE.partitionKey.name} to specify the partition key column
        Use --${AllPipelineOptions.INSTANCE.rangeKey.name} to specify the range key column
        Use --${AllPipelineOptions.INSTANCE.selectFields.name} to specify columns to fetch
        Use --${AllPipelineOptions.INSTANCE.numberOfFilesPerRequest.name} to specify how many items should be requested per backend call
    """.trimIndent()
    override fun argDescription() = "<table>"

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.RECORD,
        DynamoDBKeys.TABLE_NAME
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        dynamoDBClientFactory.createAmazonDynamoDBClient(parallelDegree, params.assumeRoleArn)
            .let { client ->
                DynamoDBScanWorker(
                    arg,
                    params.partitionKey,
                    params.rangeKey,
                    params.selectFields,
                    params.numberOfFilesPerRequest,
                    client,
                    objectMapper,
                    params.maxFilesPerThread,
                    w
                )
            }
}
