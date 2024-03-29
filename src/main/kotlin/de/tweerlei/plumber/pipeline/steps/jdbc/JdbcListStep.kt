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
package de.tweerlei.plumber.pipeline.steps.jdbc

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemStringAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.jdbc.JdbcKeys
import de.tweerlei.plumber.worker.impl.jdbc.JdbcSelectWorker
import de.tweerlei.plumber.worker.impl.jdbc.JdbcTemplateFactory
import org.springframework.stereotype.Service

@Service("jdbc-listWorker")
class JdbcListStep(
    private val jdbcTemplateFactory: JdbcTemplateFactory
): ProcessingStep {

    override val group = "JDBC"
    override val name = "Fetch JDBC rows"
    override val description = "Retrieve rows from the given JDBC table"
    override val help = """
        The key range to scan can be specified by setting the current range. If not set,
        the whole table will be listed.
        The current value will be set to the read record, which will also be available to record-* steps. 
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.primaryKey.name} specifies the PK column.
        --${AllPipelineOptions.INSTANCE.selectFields.name} specifies columns to fetch
    """.trimIndent()
    override val example = """
        jdbc-list:myTable --${AllPipelineOptions.INSTANCE.startAfterKey.name}=100 --${AllPipelineOptions.INSTANCE.stopAfterKey.name}=200
        record-get:id
        lines-write  # result: IDs of records between 100 (excl.) and 200 (incl.)
    """.trimIndent()
    override val argDescription = "<table>"
    override val argInterpolated = true

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.RECORD,
        JdbcKeys.TABLE_NAME
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        jdbcTemplateFactory.createJdbcTemplate(parallelDegree)
            .let { client ->
                JdbcSelectWorker(
                    arg.toWorkItemStringAccessor(),
                    params.primaryKey.toJdbcPrimaryKey(),
                    params.selectFields,
                    client,
                    params.maxFilesPerThread,
                    w
                )
            }
}
