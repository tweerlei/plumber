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
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemStringAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.sqs.SQSClientFactory
import de.tweerlei.plumber.worker.impl.sqs.SQSKeys
import de.tweerlei.plumber.worker.impl.sqs.SQSReceiveWorker
import org.springframework.stereotype.Service

@Service("sqs-readWorker")
class SQSReceiveStep(
    private val sqsClientFactory: SQSClientFactory
): ProcessingStep {

    override val group = "AWS SQS"
    override val name = "Receive SQS messages"
    override val description = "Receive messages from the given SQS queue"
    override val help = """
        Receives messages from SQS. The ${WellKnownKeys.NAME} of each item will be set to the message ID.
        Messages have to be deleted after reception, otherwise they will be delivered again.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.maxWaitTimeSeconds.name} specifies how long to wait for the next message.
        --${AllPipelineOptions.INSTANCE.follow.name} keeps polling even if no message is currently available.
        --${AllPipelineOptions.INSTANCE.numberOfFilesPerRequest.name} specifies how many messages to retrieve per backend call.
    """.trimIndent()
    override val example = """
        sqs-read:myQueue
        files-write:/messages
        sqs-delete
    """.trimIndent()
    override val argDescription = "<queue>"
    override val argInterpolated = true

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.NAME,
        WellKnownKeys.LAST_MODIFIED,
        SQSKeys.QUEUE_URL,
        SQSKeys.MESSAGE_ID,
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
                SQSReceiveWorker(
                    arg.toWorkItemStringAccessor(),
                    params.numberOfFilesPerRequest,
                    params.maxWaitTimeSeconds,
                    params.follow,
                    client,
                    params.maxFilesPerThread,
                    w
                )
            }
}
