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
import de.tweerlei.plumber.worker.impl.math.DivideWorker
import org.springframework.stereotype.Service

@Service("divideWorker")
class DivideStep: ProcessingStep {

    override val group = "Math"
    override val name = "Divide"
    override val description = "Divide the current value by the given value"
    override val help = """
        Both operands are evaluated as numbers.
        If the dividend is a long, the division will yield a long result, rounded down.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:1 divide:2 -> 0
        value:1.0 divide:2 -> 0.5
        value:1 divide:0 -> Infinity
        value:-1 divide:0 -> -Infinity
        value:0 divide:0 -> NaN
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
        DivideWorker(valueFor(arg).toWorkItemAccessor(), w)

    private fun valueFor(arg: String) =
        arg.ifEmpty { "1" }
}
