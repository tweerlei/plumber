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
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.xml.ToXmlWorker
import org.springframework.stereotype.Service

@Service("xml-printWorker")
class XmlEncodeStep(
    private val xmlMapper: XmlMapper
): ProcessingStep {

    override val group = "XML"
    override val name = "Serialize to XML"
    override val description = "Serialize objects to XML text"
    override val help = """
        This will encode the current node, if set. Otherwise the current value is encoded.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.elementName.name} specifies the wrapping element name.
        --${AllPipelineOptions.INSTANCE.prettyPrint.name} enables pretty printing.
    """.trimIndent()
    override val example = """
        value::'<root><foo>42</foo></root>'
        xml-parse
        xml-print --pretty-print
        lines-write  # result: <item>
                                 <foo>42</foo>
                               </item>
    """.trimIndent()
    override val argDescription = ""

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
        ToXmlWorker(
            params.elementName,
            xmlMapper,
            params.prettyPrint,
            w
        )
}
