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
package de.tweerlei.plumber.pipeline.steps.attribute

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import org.springframework.stereotype.Service

@Service("setWorker")
class SetAttributeStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Set attribute"
    override val description = "Set the given attribute to the current value"
    override val help = """
        Will not change the current value.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:2
        set:two
        plus:@two
        lines-write  # result: 4
        plus:@two
        lines-write  # result: 6
        ...
    """.trimIndent()
    override val argDescription = "<name>"
    override val argInterpolated = false

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
        SettingWorker(arg, { item -> item.get(WorkItem.DEFAULT_KEY) }, w)
}
