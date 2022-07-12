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
import de.tweerlei.plumber.pipeline.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.filter.MultithreadedWorker
import de.tweerlei.plumber.worker.xml.XmlWriteWorker
import org.springframework.stereotype.Service
import java.io.File

@Service("xml-writeWorker")
class XmlWriteStep(
    private val xmlMapper: XmlMapper
): ProcessingStep {

    override val group = "XML"
    override val name = "Write value as XML"
    override val description = "Write current value as XML object to the given file"

    override fun isValuePassThrough() = true
    override fun parallelDegreeFor(arg: String) = 1

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        XmlWriteWorker(
            File(arg),
            params.elementName,
            params.rootElementName,
            xmlMapper,
            params.prettyPrint,
            w
        )
}
