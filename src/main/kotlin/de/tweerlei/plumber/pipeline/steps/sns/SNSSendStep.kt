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
package de.tweerlei.plumber.pipeline.steps.sns

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.sns.SNSClientFactory
import de.tweerlei.plumber.worker.impl.sns.SNSPublishWorker
import org.springframework.stereotype.Service

@Service("sns-writeWorker")
class SNSSendStep(
    private val snsClientFactory: SNSClientFactory
): ProcessingStep {

    override val group = "AWS SNS"
    override val name = "Send SNS message"
    override val description = "Send a message to the given SNS topic"
    override val help = ""
    override val options = ""
    override val example = """
        files-list:/messages
        files-read
        sns-write:myTopic
    """.trimIndent()
    override val argDescription = "<topicArn>"
    override val argInterpolated = true

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        snsClientFactory.createAmazonSNSClient(parallelDegree, params.assumeRoleArn)
            .let { client ->
                SNSPublishWorker(
                    arg.toWorkItemAccessor(),
                    client,
                    w
                )
            }
}
