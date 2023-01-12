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
import de.tweerlei.plumber.worker.impl.filter.BulkWorker
import org.springframework.stereotype.Service

@Service("bulkWorker")
class BulkStep: ProcessingStep {

    override val group = "Flow control"
    override val name = "Bulk execution"
    override val description = "Execute following steps using chunks of items"
    override val help = """
        This step will queue up incoming items until the given count (if unspecified, ${AllPipelineOptions.INSTANCE.numberOfFilesPerRequest.name})
        is reached and pass them on wrapped as a single item. Such items can be processed by bulk-* steps.
    """.trimIndent()
    override fun argDescription() = "<number>"

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.WORK_ITEMS,
        WellKnownKeys.SIZE
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        BulkWorker(
            arg.toIntOrNull() ?: params.numberOfFilesPerRequest,
            w
        )
}
