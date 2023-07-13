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
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.file.toInputStreamProvider
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.csv.CsvReadWorker
import org.springframework.stereotype.Service

@Service("csv-readWorker")
class CsvReadStep(
    private val csvMapper: CsvMapper
): ProcessingStep {

    override val group = "CSV"
    override val name = "Read CSV lines from file"
    override val description = "Read CSV lines from the given file"
    override val help = """
        This is not only a shortcut for lines-read csv-parse, but also has the capability to read a header line
        and apply the field names to all read records.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.separator.name} sets the record separator character.
        --${AllPipelineOptions.INSTANCE.header.name} will read the first line as column headings.
    """.trimIndent()
    override val example = """
        csv-read:products.csv --${AllPipelineOptions.INSTANCE.header.name}
        record-get:Price
        lines-write  # result: all values in the "Price" column
    """.trimIndent()
    override val argDescription
        get() = "".toInputStreamProvider().toString()
    override val argInterpolated = false

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.PATH,
        WellKnownKeys.NAME,
        WellKnownKeys.RECORD
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        CsvReadWorker(
            arg.toInputStreamProvider(),
            csvMapper,
            params.separator,
            params.header,
            params.maxFilesPerThread,
            w
        )
}
