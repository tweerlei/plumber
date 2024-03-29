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
import de.tweerlei.plumber.worker.impl.range.InRangeWorker
import org.springframework.stereotype.Service

@Service("is-inrangeWorker")
class IsInRangeStep: ProcessingStep {

    override val group = "Ranges"
    override val name = "Compare"
    override val description = "Compare the current value to the current range resulting in a boolean"
    override val help = ""
    override val options = ""
    override val example = """
        range-reset --${AllPipelineOptions.INSTANCE.startAfterKey.name}=10 --${AllPipelineOptions.INSTANCE.stopAfterKey.name}=20
        value:10 is-inrange -> false
        value:11 is-inrange -> true
        value:20 is-inrange -> true
        value:21 is-inrange -> false
    """.trimIndent()
    override val argDescription = ""
    override val argInterpolated = false

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
        InRangeWorker(w)
}
