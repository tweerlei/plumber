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
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.expr.NegatingWorker
import org.springframework.stereotype.Service

@Service("notWorker")
class NotStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Compare"
    override val description = "Logically negate the current value"
    override val help = """
        The current value is evaluated as boolean. Examples:
    """.trimIndent()
    override val options = ""
    override val example = """
        value:false not -> true
        value:true not -> false
        value:0 not -> true
        value:123 not -> false
    """.trimIndent()
    override val argDescription = ""

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        NegatingWorker(w)
}
