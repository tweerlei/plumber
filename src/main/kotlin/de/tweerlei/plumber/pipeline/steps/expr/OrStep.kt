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
package de.tweerlei.plumber.pipeline.steps.expr

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toRequiredAttributes
import de.tweerlei.plumber.pipeline.steps.toWorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.expr.OrWorker
import org.springframework.stereotype.Service

@Service("orWorker")
class OrStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Compare"
    override val description = "Logically OR the current value with the given value"
    override val help = """
        Both operands are evaluated as booleans. Examples:
          value:false or:true -> true
          value:false or:false -> false
          value:false or:1234 -> true
          value:false or:0 -> false
    """.trimIndent()
    override fun argDescription() = valueFor("")

    override fun requiredAttributesFor(arg: String) =
        arg.toRequiredAttributes()

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        OrWorker(valueFor(arg).toWorkItemAccessor(), w)

    private fun valueFor(arg: String) =
        arg.ifEmpty { "false" }
}
