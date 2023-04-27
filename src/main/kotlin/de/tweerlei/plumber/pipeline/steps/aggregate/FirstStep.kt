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
package de.tweerlei.plumber.pipeline.steps.aggregate

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.aggregate.FirstWorker
import org.springframework.stereotype.Service

@Service("firstWorker")
class FirstStep: ProcessingStep {

    override val group = "Aggregation"
    override val name = "Take first item"
    override val description = "Pass only the very first item on to next steps"
    override val help = ""
    override val options = ""
    override val example = """
        files-list
        find:'^a.*' filter
        first
        lines-write  # result: first file name starting with 'a'
    """.trimIndent()
    override val argDescription = ""
    override val argInterpolated = false

    override fun parallelDegreeFor(arg: String) = 1

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        FirstWorker(w)
}
