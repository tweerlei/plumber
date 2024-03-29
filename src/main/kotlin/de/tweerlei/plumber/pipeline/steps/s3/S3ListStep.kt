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
import de.tweerlei.plumber.worker.impl.s3.S3Keys
import de.tweerlei.plumber.worker.impl.s3.S3ListObjectsWorker
import org.springframework.stereotype.Service

@Service("s3-listWorker")
class S3ListStep(
    private val s3ClientFactory: S3ClientFactory
): ProcessingStep {

    override val group = "AWS S3"
    override val name = "List S3 object keys"
    override val description = "List objects from the given S3 bucket"
    override val help = """
        The key range to scan can be specified by setting the current range. If not set,
        the whole bucket will be listed.
        This will NOT fetch object contents. Use s3-read:
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.numberOfFilesPerRequest.name} specifies the number of objects to retrieve per backend call.
        --${AllPipelineOptions.INSTANCE.requesterPays.name} accepts being charged with S3 access costs.
    """.trimIndent()
    override val example = """
        partitions:256 --${AllPipelineOptions.INSTANCE.startAfterKey.name}=00000000 --${AllPipelineOptions.INSTANCE.stopAfterKey.name}=FFFFFFFF --${AllPipelineOptions.INSTANCE.keyChars.name}=0123456789ABCDEF
        parallel:16
        s3-list:mybucket
        bulk
        s3-bulkdelete  # list and delete with 16 parallel workers
    """.trimIndent()
    override val argDescription = "<bucket>"
    override val argInterpolated = true

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.NAME,
        WellKnownKeys.SIZE,
        WellKnownKeys.LAST_MODIFIED,
        S3Keys.BUCKET_NAME,
        S3Keys.OBJECT_KEY
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
                S3ListObjectsWorker(
                    arg.toWorkItemStringAccessor(),
                    params.requesterPays,
                    params.numberOfFilesPerRequest,
                    client,
                    params.maxFilesPerThread,
                    w
                )
            }
}
