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
import org.springframework.jdbc.core.ResultSetExtractor
import java.sql.ResultSet

class JdbcSelectOneWorker(
    private val tableName: String,
    private val primaryKey: String,
    private val jdbcTemplate: JdbcTemplate,
    limit: Int,
    worker: Worker
): GeneratingWorker(limit, worker) {

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        selectRow(
            getTableName(item),
            item.getOptional<Any>()
        ) { rs ->
            var keepGenerating = true
            while (keepGenerating && rs.next()) {
                if (!fn(rs.toWorkItem()))
                    keepGenerating = false
            }
            null
        }
    }

    private fun getTableName(item: WorkItem) =
        tableName.ifEmpty { item.getString(JdbcKeys.TABLE_NAME) }

    private fun selectRow(table: String, id: Any?, rse: ResultSetExtractor<Any?>) {
        jdbcTemplate.query(
            "SELECT * FROM $table WHERE $primaryKey = ?",
            rse,
            id
        )
    }

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
