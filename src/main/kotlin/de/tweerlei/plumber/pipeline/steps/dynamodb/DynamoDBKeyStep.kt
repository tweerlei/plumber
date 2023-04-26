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

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBKeyWorker
import de.tweerlei.plumber.worker.impl.dynamodb.DynamoDBKeys
import org.springframework.stereotype.Service

@Service("dynamodb-keyWorker")
class DynamoDBKeyStep: ProcessingStep {

    override val group = "AWS DynamoDB"
    override val name = "Build DynamoDB key"
    override val description = "Convert item to a DynamoDB key with the specified range key"
    override val help = """
        To generate a partition key only, use: value:keyValue dynamodb-key
        To generate a partition and range key, use: value:keyValue dynamodb-key:rangeKeyValue
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
    override val argDescription = "<value>"
    override val argInterpolated = true

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.RECORD,
        DynamoDBKeys.PARTITION_KEY,
//        DynamoDBKeys.RANGE_KEY
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        DynamoDBKeyWorker(
            params.partitionKey.ifEmpty { throw IllegalArgumentException("No partition key specified") },
            params.rangeKey,
            arg.toWorkItemAccessor(),
            w
        )
}
