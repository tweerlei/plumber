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
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBGetWorker
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBKeys
import org.springframework.stereotype.Service

@Service("dynamodb-readWorker")
class DynamoDBGetStep(
    private val dynamoDBClientFactory: DynamoDBClientFactory,
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "AWS DynamoDB"
    override val name = "Fetch DynamoDB item"
    override val description = "Read an element from the given DynamoDB table"
    override val help = """
        The item key will be derived from the current record, which can be taken from a previously read DynamoDB item
        or generated by the dynamodb-key step.
        If the argument is omitted, the table name will be taken from a previously read DynamoDB item.
        The current value will be set to the read record, which will also be available to record-* steps. 
        Values in this record will be JSON nodes to support complex data types.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.partitionKey.name} specifies the partition key column
        --${AllPipelineOptions.INSTANCE.rangeKey.name} specifies the range key column
    """.trimIndent()
    override val example = """
        value:123
        dynamodb-key:2022-07-28T12:34:56Z --partition-key=ItemID --range-key=UpdatedAt
        dynamodb-get:myTable
    """.trimIndent()
    override val argDescription = "<table>"

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
                DynamoDBGetWorker(
                    arg,
                    params.partitionKey.ifEmpty { throw IllegalArgumentException("No partition key specified") },
                    params.rangeKey,
                    client,
                    objectMapper,
                    w
                )
            }
}
