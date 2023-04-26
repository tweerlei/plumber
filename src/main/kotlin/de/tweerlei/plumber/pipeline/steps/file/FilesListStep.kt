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
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemStringAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.file.FileListWorker
import org.springframework.stereotype.Service

@Service("files-listWorker")
class FilesListStep: ProcessingStep {

    override val group = "Files"
    override val name = "List files"
    override val description = "Read file names from the given directory"
    override val help = """
        File contents are not read, use files-read:
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.recursive.name} enables descending into subdirectories
    """.trimIndent()
    override val example = """
        files-list:/tmp
        get:size
        lines-write  # print sizes of all files
    """.trimIndent()
    override val argDescription = "<path>"
    override val argInterpolated = true

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.PATH,
        WellKnownKeys.NAME,
        WellKnownKeys.SIZE,
        WellKnownKeys.LAST_MODIFIED
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        FileListWorker(
            arg.toWorkItemStringAccessor(),
            params.recursive,
            params.maxFilesPerThread,
            w
        )
}
