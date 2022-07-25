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

import de.tweerlei.plumber.pipeline.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.toWorkItemValue
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.dynamodb.DynamoDBKeyWorker
import de.tweerlei.plumber.worker.dynamodb.DynamoDBKeys
import org.springframework.stereotype.Service

@Service("dynamodb-keyWorker")
class DynamoDBKeyStep: ProcessingStep {

    override val group = "AWS DynamoDB"
    override val name = "Build DynamoDB key"
    override val description = "Convert item to a DynamoDB key with the specified range key"

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.RECORD,
        DynamoDBKeys.PARTITION_KEY,
//        DynamoDBKeys.RANGE_KEY
    )

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        DynamoDBKeyWorker(
            params.partitionKey.ifEmpty { throw IllegalArgumentException("No partition key specified") },
            params.rangeKey,
            arg.toWorkItemValue(),
            w
        )
}
