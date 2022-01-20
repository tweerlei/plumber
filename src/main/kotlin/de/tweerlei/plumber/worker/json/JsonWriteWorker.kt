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
package de.tweerlei.plumber.worker.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.DelegatingWorker
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import java.io.*

class JsonWriteWorker(
    private val file: File,
    private val objectMapper: ObjectMapper,
    worker: Worker
): DelegatingWorker(worker) {

    private lateinit var generator: JsonGenerator

    override fun onOpen() {
        generator = objectMapper.createGenerator(FileOutputStream(file))
    }

    override fun doProcess(item: WorkItem): Boolean =
        when (item.containsKey(JsonKeys.JSON_NODE)) {
            true -> item.getAs<JsonNode>(JsonKeys.JSON_NODE)
            else -> item.getOptional<Any>()
        }.let { obj ->
            objectMapper.writeValue(generator, obj)
        }.let {
            true
        }

    override fun onClose() {
        generator.close()
    }
}
