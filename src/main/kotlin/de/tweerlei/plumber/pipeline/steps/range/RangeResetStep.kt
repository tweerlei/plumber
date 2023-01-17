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
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import de.tweerlei.plumber.worker.types.Range
import org.springframework.stereotype.Service

@Service("range-resetWorker")
class RangeResetStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Reset range"
    override val description = "Reset the given range to default given by --${AllPipelineOptions.INSTANCE.startAfterKey.name}/--${AllPipelineOptions.INSTANCE.stopAfterKey.name}"
    override val help = ""
    override val options = ""
    override val example = """
        range-reset --start-after=10 --stop-after=20
        range-get:start
        lines-write  # result: 10
        
        range-reset --start-after=10 --stop-after=20
        range-get:end
        lines-write  # result: 20
    """.trimIndent()
    override val argDescription = ""

    override fun producedAttributesFor(arg: String) = setOf(
        arg
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        when (arg) {
            WellKnownKeys.SECONDARY_RANGE -> Range(params.startAfterRangeKey, params.stopAfterRangeKey)
            else -> Range(params.startAfterKey, params.stopAfterKey)
        }.let { range ->
            SettingWorker(arg, { range }, w)
        }
}
