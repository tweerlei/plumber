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
package de.tweerlei.plumber.worker.impl.xml

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.tweerlei.plumber.worker.OutputStreamProvider
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import java.io.*
import javax.xml.stream.XMLOutputFactory

class XmlWriteWorker(
    private val outputStreamProvider: OutputStreamProvider,
    private val elementName: String,
    private val rootElementName: String,
    private val xmlMapper: XmlMapper,
    private val prettyPrint: Boolean,
    worker: Worker
): DelegatingWorker(worker) {

    private val writer = xmlMapper.writer().withRootName(elementName)
    private lateinit var generator: JsonGenerator

    override fun onOpen() {
        XMLOutputFactory.newInstance().createXMLStreamWriter(outputStreamProvider.open())
            .also { streamWriter ->
                generator = xmlMapper.factory.createGenerator(streamWriter)
                if (prettyPrint)
                    generator.prettyPrinter = xmlMapper.serializationConfig.constructDefaultPrettyPrinter()

                streamWriter.writeStartDocument()
                streamWriter.writeStartElement(rootElementName)
            }
    }

    override fun doProcess(item: WorkItem): Boolean =
        item.getFirst(WellKnownKeys.NODE)
            .toJsonNode()
            .let { obj ->
                writer.writeValue(generator, obj)
            }.let { true }

    override fun onClose() {
        generator.close()
    }
}
