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
import de.tweerlei.plumber.worker.impl.jdbc.JdbcRangeWorker
import de.tweerlei.plumber.worker.impl.jdbc.JdbcTemplateFactory
import org.springframework.stereotype.Service

@Service("jdbc-rangeWorker")
class JdbcRangeStep(
    private val jdbcTemplateFactory: JdbcTemplateFactory
): ProcessingStep {

    override val group = "JDBC"
    override val name = "JDBC key range"
    override val description = "Determine the actual range of values for the JDBC primaryKey, use with partition:n"
    override val help = """
        This will set the current range to the range of values in the primary key column.
    """.trimIndent()
    override val options = """
        --${AllPipelineOptions.INSTANCE.primaryKey.name} specifies the PK column.
    """.trimIndent()
    override val example = """
        jdbc-range:myTable --primary-key=id
        jdbc-delete  # delete all items
        
        jdbc-range:myTable --primary-key=id
        parallel:8
        jdbc-delete  # delete in parallel
    """.trimIndent()
    override val argDescription = "<table>"
    override val argInterpolated = true

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.RANGE,
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
                JdbcRangeWorker(
                    arg.toWorkItemStringAccessor(),
                    params.primaryKey.toJdbcPrimaryKey(),
                    client,
                    w
                )
            }
}
