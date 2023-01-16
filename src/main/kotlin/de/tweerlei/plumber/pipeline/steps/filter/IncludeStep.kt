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
package de.tweerlei.plumber.pipeline.steps.filter

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import org.springframework.stereotype.Service

@Service("includeWorker")
class IncludeStep: ProcessingStep {

    override val group = "Meta"
    override val name = "Include steps"
    override val description = "Read step definitions from a file"
    override val help = """
        You can put your whole pipeline or parts of it into a file.
        Such files are expected to contain each step or option on a separate line. Empty lines and lines starting with a # will be ignored.

        While the included steps are inserted in place of the include: step, options specified on higher levels (topmost being the command line)
        override options in included files. This also applies to options specified AFTER the include: step.
    """.trimIndent()
    override val options = ""
    override val example = """
        include:steps.txt
    """.trimIndent()
    override val argDescription = "<file>"

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        throw IllegalStateException("This step exists only for showing the help text")
}
