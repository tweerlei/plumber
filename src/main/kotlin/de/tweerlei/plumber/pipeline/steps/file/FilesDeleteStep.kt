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
package de.tweerlei.plumber.pipeline.steps.file

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemStringAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.file.FileDeleteWorker
import org.springframework.stereotype.Service

@Service("files-deleteWorker")
class FilesDeleteStep: ProcessingStep {

    override val group = "Files"
    override val name = "Delete files"
    override val description = "Delete files from the given directory"
    override val help = """
        The ${WellKnownKeys.NAME} attribute is evaluated relative to the given directory (default to the current one).
    """.trimIndent()
    override val options = ""
    override val example = """
        files-list:/originals
        files-delete:/duplicates
    """.trimIndent()
    override val argDescription = "<path>"
    override val argInterpolated = true

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.NAME
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        FileDeleteWorker(
            arg.toWorkItemStringAccessor(),
            w
        )
}
