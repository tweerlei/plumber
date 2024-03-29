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
package de.tweerlei.plumber.pipeline.steps.math

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.math.PowerWorker
import org.springframework.stereotype.Service

@Service("powerWorker")
class PowerStep: ProcessingStep {

    override val group = "Math"
    override val name = "Power"
    override val description = "Raise the current value to the given power"
    override val help = """
        Both operands are evaluated as numbers.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:10 power:2 -> 100.0
        value:10 power:-1 -> 0.01
        value:0 power:-1 -> Infinity
        value:0 power:0 -> 1.0
    """.trimIndent()
    override val argDescription
        get() = valueFor("")
    override val argInterpolated = true

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        PowerWorker(valueFor(arg).toWorkItemAccessor(), w)

    private fun valueFor(arg: String) =
        arg.ifEmpty { "1" }
}
