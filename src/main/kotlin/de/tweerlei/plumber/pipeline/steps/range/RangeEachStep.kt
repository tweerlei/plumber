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
import de.tweerlei.plumber.worker.impl.range.RangeIteratingWorker
import org.springframework.stereotype.Service

@Service("range-eachWorker")
class RangeEachStep: ProcessingStep {

    override val group = "Ranges"
    override val name = "Iterate range"
    override val description = "Generate items with the values of the input item's range using the given increment"
    override val help = """
        The types of the current range's bounds are important. Numeric bounds will generate numbers
        while string bounds will generate strings.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.keyChars.name} specifies valid characters for key range generation.
    """.trimIndent()
    override val example = """
        range-reset --start-after=10 --stop-after=13
        range-each
        lines-write  # result: 11
                               12
                               13

        range-reset --start-after=a --stop-after=g
        range-each --key-chars=aceg
        lines-write  # result: c
                               e
                               g
    """.trimIndent()
    override val argDescription
        get() = stepCountFor("").toString()

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.RANGE
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        RangeIteratingWorker(
            params.keyChars,
            stepCountFor(arg),
            params.maxFilesPerThread,
            w
        )

    private fun stepCountFor(arg: String) =
        arg.toLongOrNull() ?: 1
}
