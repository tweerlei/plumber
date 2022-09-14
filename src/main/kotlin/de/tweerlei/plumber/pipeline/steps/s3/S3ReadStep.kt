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

import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.s3.S3ClientFactory
import de.tweerlei.plumber.worker.impl.s3.S3GetObjectWorker
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.s3.S3Keys
import org.springframework.stereotype.Service

@Service("s3-readWorker")
class S3ReadStep(
    private val s3ClientFactory: S3ClientFactory
): ProcessingStep {

    override val group = "AWS S3"
    override val name = "Fetch S3 object"
    override val description = "Get an object from the given S3 bucket"
    override fun argDescription() = "<bucket>"

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.NAME
    )
    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.NAME,
        WellKnownKeys.SIZE,
        WellKnownKeys.LAST_MODIFIED,
        S3Keys.BUCKET_NAME,
        S3Keys.OBJECT_KEY
    )

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        s3ClientFactory.createAmazonS3Client(parallelDegree, params.assumeRoleArn)
            .let { client ->
                S3GetObjectWorker(
                    arg,
                    params.requesterPays,
                    client,
                    w
                )
            }
}
