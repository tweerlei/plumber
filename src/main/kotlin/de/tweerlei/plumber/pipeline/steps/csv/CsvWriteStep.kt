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
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.file.toInputFile
import de.tweerlei.plumber.pipeline.steps.file.toOutputFile
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.csv.CsvWriteWorker
import de.tweerlei.plumber.worker.types.Record
import org.springframework.stereotype.Service

@Service("csv-writeWorker")
class CsvWriteStep(
    private val csvMapper: CsvMapper
): ProcessingStep {

    override val group = "CSV"
    override val name = "Write value as CSV"
    override val description = "Write current value as CSV object to the given file"
    override fun argDescription() = "".toOutputFile().toString()

    override fun expectedInputFor(arg: String) = Record::class.java
    override fun parallelDegreeFor(arg: String) = 1

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        CsvWriteWorker(
            arg.toOutputFile(),
            csvMapper,
            params.separator,
            params.header,
            w
        )
}
