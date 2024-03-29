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

@Service("getWorker")
class GetAttributeStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Get attribute"
    override val description = "Set the current value to the given attribute"
    override val help = """
        get:myname is equivalent to value:@myname
    """.trimIndent()
    override val options = ""
    override val example = """
        files-list
        get:size
        lines-write  # result: size of each file
    """.trimIndent()
    override val argDescription = "<name>"
    override val argInterpolated = false

    override fun requiredAttributesFor(arg: String) = setOf(
        arg
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        SettingWorker(WorkItem.DEFAULT_KEY, { item -> item.get(arg) }, w)
}
