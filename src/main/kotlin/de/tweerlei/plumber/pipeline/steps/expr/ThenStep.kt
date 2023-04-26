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
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import org.springframework.stereotype.Service

@Service("thenWorker")
class ThenStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Conditionally set value"
    override val description = "Sets the current value to the given value if current value is truthy"
    override val help = """
        The current value is evaluated as boolean.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:true then:yes -> yes
        value:false then:yes -> false
        value:123 then:yes -> yes
        value:0 then:yes -> 0
    """.trimIndent()
    override val argDescription = "<value>"
    override val argInterpolated = true

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.TEST_RESULT
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        de.tweerlei.plumber.worker.impl.attribute.ConditionalWorker(
            { item -> item.get().toBoolean() },
            arg.toWorkItemAccessor(),
            w
        )
}
