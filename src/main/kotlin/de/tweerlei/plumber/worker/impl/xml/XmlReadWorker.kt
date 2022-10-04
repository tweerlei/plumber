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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.tweerlei.plumber.worker.InputStreamProvider
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.Node
import de.tweerlei.plumber.worker.types.StringValue
import de.tweerlei.plumber.worker.types.Value
import de.tweerlei.plumber.worker.types.toValue
import mu.KLogging
import java.io.InputStream
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants.START_ELEMENT

class XmlReadWorker<T>(
    private val inputStreamProvider: InputStreamProvider,
    private val elementName: String,
    private val itemType: Class<T>,
    private val xmlMapper: XmlMapper,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    private val valueType = when (itemType) {
        Any::class.java -> JsonNode::class.java
        else -> itemType
    }
    private lateinit var stream: InputStream

    override fun onOpen() {
        stream = inputStreamProvider.open()
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        XMLInputFactory.newInstance().createXMLStreamReader(stream)
            .also { logger.info { "Reading XML objects as ${valueType.simpleName}" } }
            .let { reader ->
                try {
                    val filePath = StringValue.of(inputStreamProvider.getPath())
                    val fileName = StringValue.of(inputStreamProvider.getName())
                    var keepGenerating = true
                    while (keepGenerating && reader.hasNext()) {
                        reader.next()
                        if (reader.eventType == START_ELEMENT && reader.localName == elementName) {
                            keepGenerating = xmlMapper.readValue(reader, valueType)
                                ?.toValue()
                                ?.let { obj ->
                                    fn(obj.toWorkItem(filePath, fileName))
                                } ?: false
                        }
                    }
                } finally {
                    reader.close()
                }
            }
    }

    private fun Value.toWorkItem(path: StringValue, name: StringValue) =
        WorkItem.of(
            this,
            WellKnownKeys.PATH to path,
            WellKnownKeys.NAME to name
        ).also { item ->
            if (this is Node)
                item.set(this, WellKnownKeys.NODE)
        }

    override fun onClose() {
        stream.close()
    }
}
