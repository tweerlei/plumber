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
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.filter.UnbulkWorker
import org.springframework.stereotype.Service

@Service("unbulkWorker")
class UnbulkStep: ProcessingStep {

    override val group = "Flow control"
    override val name = "Un-bulk items"
    override val description = "Split bulks into separate items again"
    override val help = """
        Split a combined item created by the bulk: step into separate items again.
    """.trimIndent()
    override val options = ""
    override val example = """
        uuid --${AllPipelineOptions.INSTANCE.maxFilesPerThread.name}=100
        bulk:10
        count
        unbulk
        count
    """.trimIndent()
    override val argDescription = ""
    override val argInterpolated = false

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.WORK_ITEMS
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        UnbulkWorker(w)
}
