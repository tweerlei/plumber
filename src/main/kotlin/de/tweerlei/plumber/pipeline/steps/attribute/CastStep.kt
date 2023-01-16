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
package de.tweerlei.plumber.pipeline.steps.attribute

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.attribute.ConvertingWorker
import de.tweerlei.plumber.worker.types.*
import org.springframework.stereotype.Service

@Service("castWorker")
class CastStep: ProcessingStep {

    override val group = "Attributes"
    override val name = "Cast value"
    override val description = "Converts the current value to the given type"
    override val help = """
        Supported types are:
          ${StringValue.NAME}
          ${LongValue.NAME}
          ${DoubleValue.NAME}
          ${BooleanValue.NAME}
          ${InstantValue.NAME}
          ${DurationValue.NAME}
          ${BigIntegerValue.NAME}
          ${BigDecimalValue.NAME}
          ${ByteArrayValue.NAME}
          ${Range.NAME} - will also set the result as current range
          ${Record.NAME} - will also set the result as current record
          ${Node.NAME} - will also set the result as current node
    """.trimIndent()
    override val options = ""
    override val example = """
        value:1.23
        cast:long
        lines-write  # result: 1
    """.trimIndent()
    override val argDescription = "<type>"

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        ConvertingWorker(arg, w)
}
