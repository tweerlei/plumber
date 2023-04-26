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
package de.tweerlei.plumber.pipeline.steps.http

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemStringAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.http.HttpDeleteWorker
import de.tweerlei.plumber.worker.impl.http.HttpKeys
import org.springframework.stereotype.Service

@Service("http-deleteWorker")
class HttpDeleteStep: ProcessingStep {

    override val group = "HTTP"
    override val name = "Delete URL"
    override val description = "Delete an object from the given URL"
    override val help = "Headers are set and returned in the ${HttpKeys.HEADERS} record."
    override val options = ""
    override val example = """
        lines-read:ids.txt
        format:'https://example.org/files/@{}'
        http-delete:@
        
        value::username:password
        text-write:base64
        format:'basic @{}'
        record-set:Authorization
        get:record set:httpHeaders
        http-delete:"https://example.org/private/file"
    """.trimIndent()
    override val argDescription = "<url>"
    override val argInterpolated = true

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.NAME,
        WellKnownKeys.SIZE,
        WellKnownKeys.LAST_MODIFIED,
        WellKnownKeys.CONTENT_TYPE,
        HttpKeys.METHOD,
        HttpKeys.URL,
        HttpKeys.HEADERS
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        HttpDeleteWorker(
            arg.toWorkItemStringAccessor(),
            w
        )
}
