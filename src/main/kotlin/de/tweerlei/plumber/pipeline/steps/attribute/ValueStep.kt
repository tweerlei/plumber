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
import de.tweerlei.plumber.pipeline.steps.toWorkItemAccessor
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.attribute.SettingWorker
import org.springframework.stereotype.Service

@Service("valueWorker")
class ValueStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Set value"
    override val description = "Sets the current value to the given value"
    override val help = """
        The argument can have one of these formats:
          value::123  -  Will always result in a string (here: "123")
          value:@name -  Will evaluate to the given attribute's value, like get:name
          value:123   -  Will be auto-converted into a suitable type (here: long)
    """.trimIndent()
    override val options = ""
    override val example = """
        value:0123
        lines-write  # result: 123
        
        value::0123
        length
        lines-write  # result: 4
        
        files-list
        value:@size
        lines-write  # result: size of each file
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
        SettingWorker(WorkItem.DEFAULT_KEY, arg.toWorkItemAccessor(), w)
}
