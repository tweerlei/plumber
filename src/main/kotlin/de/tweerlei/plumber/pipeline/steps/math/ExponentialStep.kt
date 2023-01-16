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
import de.tweerlei.plumber.pipeline.steps.toRequiredAttributes
import de.tweerlei.plumber.pipeline.steps.toWorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.math.ExponentialWorker
import org.springframework.stereotype.Service

@Service("expWorker")
class ExponentialStep: ProcessingStep {

    override val group = "Math"
    override val name = "Exponentiate"
    override val description = "Raise the given value to the power of the current value"
    override val help = """
        Both operands are evaluated as numbers.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:2 exp:10 -> 100.0
        value:-1 exp:10 -> 0.01
        value:-1 exp:0 -> Infinity
        value:0 exp:0 -> 1.0
    """.trimIndent()
    override val argDescription
        get() = baseFor("")

    override fun requiredAttributesFor(arg: String) =
        arg.toRequiredAttributes()

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        ExponentialWorker(baseFor(arg).toWorkItemAccessor(), w)

    private fun baseFor(arg: String) =
        arg.ifEmpty { Math.E.toString() }
}
