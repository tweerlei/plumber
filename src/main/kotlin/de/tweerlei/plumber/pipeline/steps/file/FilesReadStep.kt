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
import de.tweerlei.plumber.pipeline.ProcessingStep
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.file.FileKeys
import de.tweerlei.plumber.worker.file.FileReadWorker
import org.springframework.stereotype.Service

@Service("files-readWorker")
class FilesReadStep: ProcessingStep {

    override val group = "Files"
    override val name = "Read files"
    override val description = "Read files from the given base directory"

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.NAME
    )
    override fun producedAttributesFor(arg: String) = setOf(
        FileKeys.FILE_PATH,
        FileKeys.FILE_NAME
    )

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        FileReadWorker(
            arg,
            w
        )
}
