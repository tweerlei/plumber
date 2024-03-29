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
import de.tweerlei.plumber.pipeline.steps.toWorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.expr.EqualsWorker
import org.springframework.stereotype.Service

@Service("is-equalWorker")
class IsEqualStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Compare"
    override val description = "Compare the current value to the given value resulting in a boolean"
    override val help = """
        The given value is converted to the current value's type so this operation is NOT commutative.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:42 is-equal:42 -> true
        value:42 is-equal:0 -> false
        value:true is-equal:42 -> true
        value:42 is-equal:true -> false
    """.trimIndent()
    override val argDescription = "<value>"
    override val argInterpolated = true

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        EqualsWorker(arg.toWorkItemAccessor(), w)
}
