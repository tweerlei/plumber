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
package de.tweerlei.plumber.pipeline.steps.csv

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.csv.ToCsvWorker
import org.springframework.stereotype.Service

@Service("csv-printWorker")
class CsvEncodeStep(
    private val csvMapper: CsvMapper
): ProcessingStep {

    override val group = "CSV"
    override val name = "Serialize to CSV"
    override val description = "Serialize objects to CSV text"

    override fun expectedInputFor(arg: String) = Record::class.java

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        ToCsvWorker(csvMapper, params.separator, w)
}
