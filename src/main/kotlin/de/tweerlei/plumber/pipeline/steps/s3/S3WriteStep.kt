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
package de.tweerlei.plumber.pipeline.steps.s3

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemStringAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.s3.S3ClientFactory
import de.tweerlei.plumber.worker.impl.s3.S3PutObjectWorker
import org.springframework.stereotype.Service

@Service("s3-writeWorker")
class S3WriteStep(
    private val s3ClientFactory: S3ClientFactory
): ProcessingStep {

    override val group = "AWS S3"
    override val name = "Write S3 object"
    override val description = "Put an object into the given S3 bucket"
    override val help = ""
    override val options = """
        --${AllPipelineOptions.INSTANCE.requesterPays.name} accepts being charged with S3 access costs.
    """.trimIndent()
    override val example = """
        files-list:/s3dump
        files-read
        s3-write:mybucket
    """.trimIndent()
    override val argDescription = "<bucket>"
    override val argInterpolated = true

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.NAME
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        s3ClientFactory.createAmazonS3Client(parallelDegree, params.assumeRoleArn)
            .let { client ->
                S3PutObjectWorker(
                    arg.toWorkItemStringAccessor(),
                    params.requesterPays,
                    client,
                    w
                )
            }
}
