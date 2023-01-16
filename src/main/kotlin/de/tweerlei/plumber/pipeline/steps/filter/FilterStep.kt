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
package de.tweerlei.plumber.pipeline.steps.filter

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.filter.FilteringWorker
import org.springframework.stereotype.Service

@Service("filterWorker")
class FilterStep: ProcessingStep {

    override val group = "Flow control"
    override val name = "Filter items"
    override val description = "Keep only items that evaluate to the given boolean"
    override val help = """
        The current value will be evaluated as boolean and compared to the argument.
        If it does not match, the item will be discarded.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:false filter:false -> item will be passed on
        value:false filter:true -> item will be discarded
        value:true filter:true -> item will be passed on
        value:1 filter:true -> item will be passed on
        value:0 filter:true -> item will be discarded
    """.trimIndent()
    override val argDescription
        get() = compareWithFor("").toString()

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        compareWithFor(arg).let { compareWith ->
            FilteringWorker({ item ->
                item.get().toBoolean() == compareWith
            }, w)
        }

    private fun compareWithFor(arg: String) =
        arg.toBooleanStrictOrNull() ?: true
}
