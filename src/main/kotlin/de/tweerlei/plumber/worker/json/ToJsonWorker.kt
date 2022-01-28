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

import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import java.io.StringWriter

class ToJsonWorker(
    private val objectMapper: ObjectMapper,
    private val prettyPrint: Boolean,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getOptional()
            .let { obj -> writeValue(obj) }
            .also { str -> item.set(str) }
            .let { true }

    private fun writeValue(obj: Any?) =
        StringWriter().also { writer ->
            objectMapper.createGenerator(writer).also { generator ->
                if (prettyPrint)
                    generator.prettyPrinter = objectMapper.serializationConfig.constructDefaultPrettyPrinter()
                objectMapper.writeValue(generator, obj)
                    }
        }.toString()
}
