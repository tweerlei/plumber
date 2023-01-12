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
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import org.springframework.stereotype.Service

@Service("elseWorker")
class ElseStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Conditionally set value"
    override val description = "Sets the current value to the given value if a previous then: did not match"
    override val help = """
        Examples:
          value:true then:yes else:no -> yes
          value:false then:yes else:no -> no
          value:123 then:yes else:no -> yes
          value:0 then:yes else:no -> no
    """.trimIndent()
    override fun argDescription() = "<value>"

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.TEST_RESULT,
    ).plus(arg.toRequiredAttributes())

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        de.tweerlei.plumber.worker.impl.attribute.ConditionalWorker(
            { item -> item.get(WellKnownKeys.TEST_RESULT).toBoolean().not() },
            arg.toWorkItemAccessor(),
            w
        )
}
