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
import de.tweerlei.plumber.worker.impl.filter.ErrorWorker
import org.springframework.stereotype.Service

@Service("errorWorker")
class ErrorStep: ProcessingStep {

    override val group = "Logging"
    override val name = "Throw error"
    override val description = "Throw an error every given number of items"
    override val help = ""
    override val options = ""
    override val example = """
        error:2
    """.trimIndent()
    override val argDescription
        get() = intervalFor("").toString()
    override val argInterpolated = false

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        ErrorWorker(
            predecessorName,
            intervalFor(arg),
            w
        )

    private fun intervalFor(arg: String) =
        arg.toLongOrNull() ?: 1
}
