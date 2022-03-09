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
package de.tweerlei.plumber.worker.jdbc

import de.tweerlei.plumber.worker.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class JdbcSelectOneWorker(
    private val tableName: String,
    private val primaryKey: String,
    private val jdbcTemplate: JdbcTemplate,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        selectRow(
            item.getIfEmpty(tableName, JdbcKeys.TABLE_NAME),
            item.getOptional()
        ) { rs, _ ->
            rs.toRecord()
        }?.also { map ->
            item.set(map)
            item.set(map, WellKnownKeys.RECORD)
            item.set(tableName, JdbcKeys.TABLE_NAME)
            item.set(map[primaryKey], JdbcKeys.PRIMARY_KEY)
        }?.let { true }
        ?: false

    private fun selectRow(table: String, id: Any?, rse: RowMapper<Record>) =
        jdbcTemplate.queryForObject(
            "SELECT * FROM $table WHERE $primaryKey = ?",
            rse,
            id
        )

    private fun ResultSet.toRecord() =
        Record().also { map ->
                for (i in 1..metaData.columnCount) {
                    map[metaData.getColumnName(i)] = getObject(i)
                }
            }
}
