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
import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.coerceToString

class FromXmlWorker<T>(
    private val itemType: Class<T>,
    private val xmlMapper: XmlMapper,
    worker: Worker
): DelegatingWorker(worker) {

    private val valueType = when (itemType) {
        Any::class.java -> JsonNode::class.java
        else -> itemType
    }

    override fun doProcess(item: WorkItem) =
        item.getOptional().coerceToString()
            .let { value ->
                xmlMapper.readValue(value, valueType)
                    ?.also { obj ->
                        item.set(obj)
                        if (obj is JsonNode)
                            item.set(obj, WellKnownKeys.NODE)
                    }
            }?.let { true }
            ?: false
}
