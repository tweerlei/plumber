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

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import java.io.*

class JsonWriteWorker(
    private val file: File,
    private val objectMapper: ObjectMapper,
    private val prettyPrint: Boolean,
    worker: Worker
): DelegatingWorker(worker) {

    private lateinit var generator: JsonGenerator
    private lateinit var stream: OutputStream
    private var firstItem: Boolean = true

    override fun onOpen() {
        stream = FileOutputStream(file)
        stream.write('['.code)
        generator = objectMapper.createGenerator(stream)
        if (prettyPrint)
            generator.prettyPrinter = objectMapper.serializationConfig.constructDefaultPrettyPrinter()
    }

    override fun doProcess(item: WorkItem): Boolean =
        item.getFirst(WellKnownKeys.NODE).toAny()
            .let { obj ->
                if (firstItem)
                    firstItem = false
                else
                    stream.write(','.code)
                objectMapper.writeValue(generator, obj)
            }.let { true }

    override fun onClose() {
        stream.write(']'.code)
        generator.close()
    }
}
