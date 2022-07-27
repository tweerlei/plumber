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
package de.tweerlei.plumber.worker.impl.jdbc

import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.types.Range
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import java.sql.ResultSet

class JdbcSelectWorker(
    private val tableName: String,
    private val primaryKey: String,
    private val jdbcTemplate: JdbcTemplate,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val range = item.getOptionalAs<Range>(WellKnownKeys.RANGE)
        val startAfter = range?.startAfter
        val endWith = range?.endWith
        val table = tableName.ifEmptyGetFrom(item, JdbcKeys.TABLE_NAME)
        val extractRows = ResultSetExtractor<Int> { rs ->
            var keepGenerating = true
            var itemCount = 0
            while (keepGenerating && rs.next()) {
                if (fn(rs.toWorkItem()))
                    itemCount++
                else
                    keepGenerating = false
            }
            itemCount
        }

        val itemCount = when {
            startAfter != null && endWith != null -> selectRange(table, startAfter, endWith, extractRows)
            startAfter != null -> selectFrom(table, startAfter, extractRows)
            endWith != null -> selectTo(table, endWith, extractRows)
            else -> selectAll(table, extractRows)
        }

        logger.info { "fetched $itemCount rows" }
    }

    private fun selectAll(table: String, rse: ResultSetExtractor<Int>) =
        jdbcTemplate.query(
            "SELECT * FROM $table",
            rse
        )

    private fun selectFrom(table: String, startAfter: Any, rse: ResultSetExtractor<Int>) =
        jdbcTemplate.query(
            "SELECT * FROM $table WHERE $primaryKey > ?",
            rse,
            startAfter
        )

    private fun selectTo(table: String, endWith: Any, rse: ResultSetExtractor<Int>) =
        jdbcTemplate.query(
            "SELECT * FROM $table WHERE $primaryKey <= ?",
            rse,
            endWith
        )

    private fun selectRange(table: String, startAfter: Any, endWith: Any, rse: ResultSetExtractor<Int>) =
        jdbcTemplate.query(
            "SELECT * FROM $table WHERE $primaryKey > ? AND $primaryKey <= ?",
            rse,
            startAfter,
            endWith
        )

    private fun ResultSet.toWorkItem() =
        Record()
            .also { map ->
                for (i in 1..metaData.columnCount) {
                    map[metaData.getColumnName(i)] = getObject(i)
                }
            }.let { map ->
                WorkItem.of(
                    map,
                    WellKnownKeys.RECORD to map,
                    JdbcKeys.TABLE_NAME to tableName,
                    JdbcKeys.PRIMARY_KEY to map[primaryKey]
                )
            }
}
