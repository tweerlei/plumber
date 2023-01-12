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
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.file.toOutputStreamProvider
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.json.JsonWriteWorker
import org.springframework.stereotype.Service

@Service("json-writeWorker")
class JsonWriteStep(
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "JSON"
    override val name = "Write value as JSON"
    override val description = "Write current value as JSON object to the given file"
    override val help = """
        This will encode the current node, if set. Otherwise the current value is encoded.
        Use --${AllPipelineOptions.INSTANCE.prettyPrint.name} to enable pretty printing.
        Use --${AllPipelineOptions.INSTANCE.wrapRoot} and --${AllPipelineOptions.INSTANCE.rootElementName} to wrap the result
        as a single property of an outer JSON object.
    """.trimIndent()
    override fun argDescription() = "".toOutputStreamProvider().toString()

    override fun parallelDegreeFor(arg: String) = 1

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        JsonWriteWorker(
            arg.toOutputStreamProvider(),
            objectMapper,
            when (params.wrapRoot) {
                true -> params.rootElementName
                else -> null
            },
            params.prettyPrint,
            w
        )
}
