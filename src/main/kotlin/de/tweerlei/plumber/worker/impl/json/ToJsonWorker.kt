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

import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.StringValue
import java.io.StringWriter

class ToJsonWorker(
    private val objectMapper: ObjectMapper,
    private val prettyPrint: Boolean,
    worker: Worker
): DelegatingWorker(worker) {

    companion object {
        val CONTENT_TYPE_JSON = StringValue.of("application/json")
    }

    override fun doProcess(item: WorkItem) =
        item.get().toJsonNode()
            .let { obj -> writeValue(obj) }
            .also { str ->
                item.set(StringValue.of(str))
                item.set(CONTENT_TYPE_JSON, WellKnownKeys.CONTENT_TYPE)
            }
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
