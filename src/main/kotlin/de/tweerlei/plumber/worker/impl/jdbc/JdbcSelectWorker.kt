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
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import de.tweerlei.plumber.worker.types.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import java.sql.ResultSet

class JdbcSelectWorker(
    private val tableName: String,
    private val primaryKey: String,
    private val selectFields: Set<String>,
    private val jdbcTemplate: JdbcTemplate,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val range = item.getOptionalAs(WellKnownKeys.RANGE) ?: Range()
        val table = tableName.ifEmptyGetFrom(item, JdbcKeys.TABLE_NAME)
        logger.info { "fetching elements from ${range.startAfter} to ${range.endWith}" }

        val extractRows = ResultSetExtractor<Int> { rs ->
            var keepGenerating = true
            var itemCount = 0
            while (keepGenerating && rs.next()) {
                if (fn(rs.toWorkItem(table)))
                    itemCount++
                else
                    keepGenerating = false
            }
            itemCount
        }

        val itemCount = when {
            range.startAfter !is NullValue && range.endWith !is NullValue -> selectRange(table, range.startAfter, range.endWith, extractRows)
            range.startAfter !is NullValue -> selectFrom(table, range.startAfter, extractRows)
            range.endWith !is NullValue -> selectTo(table, range.endWith, extractRows)
            else -> selectAll(table, extractRows)
        }

        logger.info { "fetched $itemCount rows from ${range.startAfter} to ${range.endWith}" }
    }

    private fun selectAll(table: String, rse: ResultSetExtractor<Int>) =
        jdbcTemplate.query(
            "SELECT ${fieldsToSelect()} FROM $table",
            rse
        )

    private fun selectFrom(table: String, startAfter: ComparableValue, rse: ResultSetExtractor<Int>) =
        jdbcTemplate.query(
            "SELECT ${fieldsToSelect()} FROM $table WHERE $primaryKey > ?",
            rse,
            startAfter.toAny()
        )

    private fun selectTo(table: String, endWith: ComparableValue, rse: ResultSetExtractor<Int>) =
        jdbcTemplate.query(
            "SELECT ${fieldsToSelect()} FROM $table WHERE $primaryKey <= ?",
            rse,
            endWith.toAny()
        )

    private fun selectRange(table: String, startAfter: ComparableValue, endWith: ComparableValue, rse: ResultSetExtractor<Int>) =
        jdbcTemplate.query(
            "SELECT ${fieldsToSelect()} FROM $table WHERE $primaryKey > ? AND $primaryKey <= ?",
            rse,
            startAfter.toAny(),
            endWith.toAny()
        )

    private fun fieldsToSelect() =
        selectFields.ifEmpty { setOf("*") }.joinToString(", ")

    private fun ResultSet.toWorkItem(actualTableName: String) =
        Record()
            .also { map ->
                for (i in 1..metaData.columnCount) {
                    map[metaData.getColumnName(i)] = getObject(i).toValue()
                }
            }.let { map ->
                WorkItem.from(
                    map,
                    WellKnownKeys.RECORD to map,
                    JdbcKeys.TABLE_NAME to actualTableName,
                    JdbcKeys.PRIMARY_KEY to map[primaryKey]
                )
            }
}
