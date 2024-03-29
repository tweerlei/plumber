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
package de.tweerlei.plumber.pipeline.steps.json

import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.file.toInputStreamProvider
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.json.JsonReadWorker
import org.springframework.stereotype.Service

@Service("json-readWorker")
class JsonReadStep(
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "JSON"
    override val name = "Read JSON objects from file"
    override val description = "Read JSON objects from the given file"
    override val help = """
        The file can contain multiple JSON objects in concatenation.
        JSON arrays will NOT be split into separate items.
    """.trimIndent()
    override val options = ""
    override val example = """
        json-read:items.json
        node-get:id
        lines-write  # result: id value of each JSON object
    """.trimIndent()
    override val argDescription
        get() = "".toInputStreamProvider().toString()
    override val argInterpolated = false

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.PATH,
        WellKnownKeys.NAME,
        WellKnownKeys.NODE
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        JsonReadWorker(
            arg.toInputStreamProvider(),
            objectMapper,
            params.maxFilesPerThread,
            w
        )
}
