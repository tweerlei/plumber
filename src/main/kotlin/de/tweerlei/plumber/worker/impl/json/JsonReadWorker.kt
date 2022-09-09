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
package de.tweerlei.plumber.worker.impl.json

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import mu.KLogging
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class JsonReadWorker<T>(
    private val file: File,
    private val itemType: Class<T>,
    private val objectMapper: ObjectMapper,
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
        stream = FileInputStream(file)
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        JsonFactory().createParser(stream)
            .also { logger.info { "Reading JSON objects as ${valueType.simpleName}" } }
            .let { parser -> objectMapper.readValues(parser, valueType) }
            .use {
                var keepGenerating = true
                while (keepGenerating && it.hasNextValue()) {
                    keepGenerating = it.nextValue()
                        ?.let { obj ->
                            fn(obj.toWorkItem())
                        } ?: false
                }
            }
    }

    private fun Any.toWorkItem() =
        WorkItem.of(
            this,
            WellKnownKeys.PATH to file.parentFile?.absolutePath,
            WellKnownKeys.NAME to file.name
        ).also { item ->
            if (this is JsonNode)
                item.set(this, WellKnownKeys.NODE)
        }

    override fun onClose() {
        stream.close()
    }
}