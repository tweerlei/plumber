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
package de.tweerlei.plumber.pipeline.steps.text

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.text.MatchingWorker
import de.tweerlei.plumber.worker.impl.text.TextKeys
import org.springframework.stereotype.Service

@Service("findWorker")
class FindStep: ProcessingStep {

    override val group = "Text"
    override val name = "Find by regex"
    override val description = "Find matches of the given regular expression, use with filter: or replace:"
    override val help = """
        The current value will be set to the matched substring.
    """.trimIndent()
    override fun argDescription() = "<regex>"

    override fun producedAttributesFor(arg: String) = setOf(
        TextKeys.MATCH_EXPRESSION,
        TextKeys.MATCH_INPUT
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        MatchingWorker(Regex(arg), w)
}
