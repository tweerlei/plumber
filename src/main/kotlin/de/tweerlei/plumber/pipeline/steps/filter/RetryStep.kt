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
package de.tweerlei.plumber.pipeline.steps.filter

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.filter.RetryingWorker
import org.springframework.stereotype.Service

@Service("retryWorker")
class RetryStep: ProcessingStep {

    override val group = "Flow control"
    override val name = "Retry"
    override val description = "Retry the following steps a given number of times on error"
    override val help = """
        If one of the following steps throws an error, perform the given number of retry attempts.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.retryDelaySeconds} adds a delay between retries.
        --${AllPipelineOptions.INSTANCE.failFast} stops processing more items when no retry succeeded.
    """.trimIndent()
    override val example = """
        uuid --${AllPipelineOptions.INSTANCE.maxFilesPerThread.name}=10
        retry:2
        error:2
        lines-write
    """.trimIndent()
    override val argDescription
        get() = retryCountFor("").toString()
    override val argInterpolated = false

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        RetryingWorker(
            predecessorName,
            retryCountFor(arg),
            params.retryDelaySeconds,
            w
        )

    private fun retryCountFor(arg: String) =
        arg.toLongOrNull() ?: Long.MAX_VALUE
}
