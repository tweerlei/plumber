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
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.text.UUIDWorker
import org.springframework.stereotype.Service

@Service("uuidWorker")
class UuidStep: ProcessingStep {

    override val group = "Text"
    override val name = "Generate UUIDs"
    override val description = "Generate random UUIDs"
    override val help = ""
    override val options = ""
    override val example = """
        uuid --${AllPipelineOptions.INSTANCE.maxFilesPerThread.name}=1
        lines-write  # result: 563de642-9a29-4804-b13c-1d5b129b47f6
    """.trimIndent()
    override val argDescription = ""
    override val argInterpolated = false

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        UUIDWorker(params.maxFilesPerThread, w)
}
