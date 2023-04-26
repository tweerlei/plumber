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
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.toWorkItemStringAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.jdbc.JdbcInsertWorker
import de.tweerlei.plumber.worker.impl.jdbc.JdbcTemplateFactory
import org.springframework.stereotype.Service

@Service("jdbc-writeWorker")
class JdbcWriteStep(
    private val jdbcTemplateFactory: JdbcTemplateFactory
): ProcessingStep {

    override val group = "JDBC"
    override val name = "Insert JDBC rows"
    override val description = "Insert rows into the given JDBC table"
    override val help = """
        This will write the current record, if any. Otherwise the current value will be used.
        If the argument is omitted, the table name will be taken from a previously read JDBC item.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:42
        record-set:id
        jdbc-read:myTable --primary-key=id
        value:Joe
        record-set:name
        jdbc-write  # update name to 'Joe'
    """.trimIndent()
    override val argDescription = "<table>"
    override val argInterpolated = true

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        jdbcTemplateFactory.createJdbcTemplate(parallelDegree)
            .let { client ->
                JdbcInsertWorker(
                    arg.toWorkItemStringAccessor(),
                    client,
                    w
                )
            }
}
