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
import de.tweerlei.plumber.pipeline.steps.toWorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.http.HttpGetWorker
import de.tweerlei.plumber.worker.impl.http.HttpKeys
import org.springframework.stereotype.Service

@Service("http-readWorker")
class HttpReadStep: ProcessingStep {

    override val group = "HTTP"
    override val name = "Fetch URL"
    override val description = "Get an object from the given URL"
    override val help = "Headers are set and returned in the ${HttpKeys.HEADERS} record."
    override val options = ""
    override val example = """
        http-read:"https://example.org/some/file"
        files-write:/dump
        
        value::username:password
        text-write:base64
        format:'basic @{}'
        record-set:Authorization
        get:record set:httpHeaders
        http-read:"https://example.org/private/file"
        lines-write  # Contents of private file
    """.trimIndent()
    override val argDescription = "<url>"

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
        HttpGetWorker(
            arg.toWorkItemAccessor(),
            w
        )
}
