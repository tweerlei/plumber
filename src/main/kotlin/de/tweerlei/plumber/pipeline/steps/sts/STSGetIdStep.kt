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
package de.tweerlei.plumber.pipeline.steps.sts

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.sts.STSClientFactory
import de.tweerlei.plumber.worker.impl.sts.STSGetIdWorker
import org.springframework.stereotype.Service

@Service("sts-accountidWorker")
class STSGetIdStep(
    private val stsClientFactory: STSClientFactory
): ProcessingStep {

    override val group = "AWS STS"
    override val name = "Get account ID"
    override val description = "Get the effective AWS account ID"

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        stsClientFactory.createAmazonSTSClient(parallelDegree, params.assumeRoleArn)
            .let { client ->
                STSGetIdWorker(
                    client,
                    w
                )
            }
}
