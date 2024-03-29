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
package de.tweerlei.plumber.pipeline.steps.sqs

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemStringAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.sqs.SQSClientFactory
import de.tweerlei.plumber.worker.impl.sqs.SQSDeleteWorker
import de.tweerlei.plumber.worker.impl.sqs.SQSKeys
import org.springframework.stereotype.Service

@Service("sqs-deleteWorker")
class SQSDeleteStep(
    private val sqsClientFactory: SQSClientFactory
): ProcessingStep {

    override val group = "AWS SQS"
    override val name = "Delete SQS message"
    override val description = "Delete a message from the given SQS queue"
    override val help = """
        The required delete handle is usually acquired by reading the message from the SQS queue first.
    """.trimIndent()
    override val options = ""
    override val example = """
        sqs-read:myQueue
        sqs-delete
    """.trimIndent()
    override val argDescription = "<queue>"
    override val argInterpolated = true

    override fun requiredAttributesFor(arg: String) = setOf(
        SQSKeys.DELETE_HANDLE
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        sqsClientFactory.createAmazonSQSClient(parallelDegree, params.assumeRoleArn)
            .let { client ->
                SQSDeleteWorker(
                    arg.toWorkItemStringAccessor(),
                    client,
                    w
                )
            }
}
