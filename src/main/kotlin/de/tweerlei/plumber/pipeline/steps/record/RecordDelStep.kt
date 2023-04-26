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
package de.tweerlei.plumber.pipeline.steps.record

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.record.RecordUnsetWorker
import org.springframework.stereotype.Service

@Service("record-delWorker")
class RecordDelStep: ProcessingStep {

    override val group = "Records"
    override val name = "Remove record field"
    override val description = "Remove the given field from the current record"
    override val help = """
        The current record will be modified.
    """.trimIndent()
    override val options = ""
    override val example = """
        value::alice,bob,charlie
        csv-parse
        record-del:1
        csv-write  # result: alice,bob
    """.trimIndent()
    override val argDescription = "<name>"
    override val argInterpolated = false

    override fun requiredAttributesFor(arg: String) = setOf(
        WellKnownKeys.RECORD
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        RecordUnsetWorker(arg, w)
}
