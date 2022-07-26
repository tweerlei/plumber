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
package de.tweerlei.plumber.pipeline.steps.stats

import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.impl.stats.HistogramWorker
import de.tweerlei.plumber.util.KeyRangeGenerator
import de.tweerlei.plumber.worker.Worker
import org.springframework.stereotype.Service

@Service("histogramWorker")
class HistogramStep: ProcessingStep {

    override val group = "Logging"
    override val name = "Histogram"
    override val description = "Build a histogram with the given number of buckets"

    override fun isValuePassThrough() = true
    override fun parallelDegreeFor(arg: String) = 1

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        KeyRangeGenerator(params.keyChars)
            .let { gen ->
                HistogramWorker(
                    predecessorName,
                    arg.toIntOrNull() ?: 10,
                    gen.packer,
                    gen.extractPrefix(params.startAfterKey, params.stopAfterKey),
                    w
                )
            }
}
