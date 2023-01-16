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
        A plain JSON array of items will be written to the output file.
        Use json-print lines-write to just concatenate JSON objects.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.prettyPrint.name} enables pretty printing.
        --${AllPipelineOptions.INSTANCE.wrapRoot.name} enable wrapping the JSON object in an outer object
        --${AllPipelineOptions.INSTANCE.rootElementName.name} specifies the property name to create in the outer object
    """.trimIndent()
    override val example = """
        uuid --limit=2
        node-set:uuid
        json-write  # result: [{"uuid":"3170d9fc-6e75-4b76-8d9a-e33cc93a160d"}, {"uuid":"368cf6d6-120a-4e31-a717-c52ed08ce7cd"}]
        
        uuid --limit=2
        node-set:uuid
        json-write --pretty-print  # result: [{
                                               "uuid":"3170d9fc-6e75-4b76-8d9a-e33cc93a160d"
                                             }, {
                                               "uuid":"368cf6d6-120a-4e31-a717-c52ed08ce7cd"}
                                             ]
        
        uuid --limit=2
        node-set:uuid
        json-write --pretty-print --wrap-root  # result: {"items":[{
                                                           "uuid":"3170d9fc-6e75-4b76-8d9a-e33cc93a160d"
                                                         }, {
                                                           "uuid":"368cf6d6-120a-4e31-a717-c52ed08ce7cd"}
                                                         }]}
    """.trimIndent()
    override val argDescription
        get() = "".toOutputStreamProvider().toString()

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
