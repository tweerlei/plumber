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
package de.tweerlei.plumber.pipeline.steps.range

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.range.KeyRangeWorker
import org.springframework.stereotype.Service

@Service("partitionsWorker")
class PartitionsStep: ProcessingStep {

    override val group = "Ranges"
    override val name = "Generate partitions"
    override val description = "Generate key ranges for n partitions, use with parallel:<n>"
    override val help = """
        This will split the current range into (at most) the given number of sub ranges
        and pass these on to the following steps.
        The types of the current range's bounds are important. Numeric bounds will be split into number ranges
        while string bounds will generate key ranges.
        Use --${AllPipelineOptions.INSTANCE.keyChars.name} to specify valid characters for key range generation.
    """.trimIndent()
    override fun argDescription() = partitionCountFor("").toString()

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.RANGE
    )
    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.RANGE
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        KeyRangeWorker(
            partitionCountFor(arg),
            params.keyChars,
            params.maxFilesPerThread,
            w
        )

    private fun partitionCountFor(arg: String) =
        arg.toIntOrNull() ?: 8
}
