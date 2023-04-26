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
import de.tweerlei.plumber.worker.impl.math.TimesWorker
import org.springframework.stereotype.Service

@Service("multiplyWorker")
class MultiplyStep: ProcessingStep {

    override val group = "Math"
    override val name = "Multiply"
    override val description = "Multiply the current value with the given value"
    override val help = """
        Both operands are evaluated as numbers.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:10 multiply:2.5 -> 25.0
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
        TimesWorker(valueFor(arg).toWorkItemAccessor(), w)

    private fun valueFor(arg: String) =
        arg.ifEmpty { "1" }
}
