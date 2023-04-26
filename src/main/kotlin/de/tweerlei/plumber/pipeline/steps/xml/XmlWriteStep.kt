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
import de.tweerlei.plumber.pipeline.steps.file.toOutputStreamProvider
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.xml.XmlWriteWorker
import org.springframework.stereotype.Service

@Service("xml-writeWorker")
class XmlWriteStep(
    private val xmlMapper: XmlMapper
): ProcessingStep {

    override val group = "XML"
    override val name = "Write value as XML"
    override val description = "Write current value as XML object to the given file"
    override val help = """
        This will encode the current node, if set. Otherwise the current value is encoded.
        All items will we wrapped in a root XML element.
        Use xml-pring lines-write to just concatenate XML elements.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.prettyPrint.name} enables pretty printing.
        --${AllPipelineOptions.INSTANCE.elementName.name} specifies the wrapping element name per item.
        --${AllPipelineOptions.INSTANCE.rootElementName.name} specifies the document root element name.
    """.trimIndent()
    override val example = """
        uuid --limit=2
        node-set:uuid
        xml-write  # result: <items><item><uuid>3170d9fc-6e75-4b76-8d9a-e33cc93a160d</uuid></item>
                             <item><uuid>368cf6d6-120a-4e31-a717-c52ed08ce7cd</uuid></item></items>
        
        uuid --limit=2
        node-set:uuid
        xml-write --pretty-print  # result: <items>
                                              <item>
                                                <uuid>3170d9fc-6e75-4b76-8d9a-e33cc93a160d</uuid>
                                              </item>
                                              <item>
                                                <uuid>368cf6d6-120a-4e31-a717-c52ed08ce7cd</uuid>
                                              </item>
                                            </items>
    """.trimIndent()
    override val argDescription
        get() = "".toOutputStreamProvider().toString()
    override val argInterpolated = false

    override fun parallelDegreeFor(arg: String) = 1

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        XmlWriteWorker(
            arg.toOutputStreamProvider(),
            params.elementName,
            params.rootElementName,
            xmlMapper,
            params.prettyPrint,
            w
        )
}
