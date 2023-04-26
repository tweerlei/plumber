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
package de.tweerlei.plumber.pipeline.steps.xml

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.file.toInputStreamProvider
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.xml.XmlReadWorker
import org.springframework.stereotype.Service

@Service("xml-readWorker")
class XmlReadStep(
    private val xmlMapper: XmlMapper
): ProcessingStep {

    override val group = "XML"
    override val name = "Read XML objects from file"
    override val description = "Read XML objects from the given file"
    override val help = ""
    override val options = ""
    override val example = """
        xml-read:items.xml
        node-get:id
        lines-write  # result: id value of each XML object
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
        XmlReadWorker(
            arg.toInputStreamProvider(),
            params.elementName,
            xmlMapper,
            params.maxFilesPerThread,
            w
        )
}
