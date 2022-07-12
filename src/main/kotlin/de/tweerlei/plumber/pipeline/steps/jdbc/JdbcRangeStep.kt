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

import de.tweerlei.plumber.pipeline.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.jdbc.JdbcKeys
import de.tweerlei.plumber.worker.jdbc.JdbcRangeWorker
import de.tweerlei.plumber.worker.jdbc.JdbcTemplateFactory
import org.springframework.stereotype.Service

@Service("jdbc-rangeWorker")
class JdbcRangeStep(
    private val jdbcTemplateFactory: JdbcTemplateFactory
): ProcessingStep {

    override val group = "JDBC"
    override val name = "JDBC key range"
    override val description = "Determine the actual range of values for the JDBC primaryKey, use with partition:n"

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.START_AFTER_KEY,
        WellKnownKeys.END_WITH_KEY,
        JdbcKeys.TABLE_NAME
    )

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        jdbcTemplateFactory.createJdbcTemplate(parallelDegree)
            .let { client ->
                JdbcRangeWorker(
                    arg,
                    params.primaryKey.ifEmpty { "id" },
                    client,
                    w
                )
            }
}
