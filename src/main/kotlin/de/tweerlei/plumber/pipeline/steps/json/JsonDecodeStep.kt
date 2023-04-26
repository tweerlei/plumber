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
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.json.FromJsonWorker
import org.springframework.stereotype.Service

@Service("json-parseWorker")
class JsonDecodeStep(
    private val objectMapper: ObjectMapper
): ProcessingStep {

    override val group = "JSON"
    override val name = "Deserialize from JSON"
    override val description = "Deserialize objects from JSON text"
    override val help = """
        The current value will be set to the JSON node, which will also be available to node-* steps. 
    """.trimIndent()
    override val options = ""
    override val example = """
        value::'{"foo":42,"bar":true}'
        json-parse
        node-get:foo
        lines-write  # result: 42
    """.trimIndent()
    override val argDescription = ""
    override val argInterpolated = false

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.NODE
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        FromJsonWorker(objectMapper, w)
}
