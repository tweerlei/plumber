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

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import java.io.StringWriter

class ToXmlWorker(
    private val elementName: String,
    private val xmlMapper: XmlMapper,
    private val prettyPrint: Boolean,
    worker: Worker
): DelegatingWorker(worker) {

    companion object {
        const val CONTENT_TYPE_XML = "application/xml"
    }

    private val writer = xmlMapper.writer().withRootName(elementName)

    override fun doProcess(item: WorkItem) =
        item.getOptional()
            .let { obj -> writeValue(obj) }
            .also { str ->
                item.set(str)
                item.set(WellKnownKeys.CONTENT_TYPE, CONTENT_TYPE_XML)
            }
            .let { true }

    private fun writeValue(obj: Any?) =
        StringWriter().also { sw ->
            xmlMapper.createGenerator(sw).also { generator ->
                if (prettyPrint)
                    generator.prettyPrinter = xmlMapper.serializationConfig.constructDefaultPrettyPrinter()
                writer.writeValue(generator, obj)
            }
        }.toString()
}
