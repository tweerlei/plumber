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
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import de.tweerlei.plumber.worker.types.StringValue
import de.tweerlei.plumber.worker.types.Value
import de.tweerlei.plumber.worker.types.toValue
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
        tableName.ifEmptyGetFrom(item, JdbcKeys.TABLE_NAME)
            .let { actualTableName ->
                selectRow(
                    actualTableName,
                    item.get()
                ) { rs, _ ->
                    rs.toRecord()
                }?.also { record ->
                    item.set(record)
                    item.set(record, WellKnownKeys.RECORD)
                    item.set(StringValue.of(actualTableName), JdbcKeys.TABLE_NAME)
                    item.set(record.getValue(primaryKey), JdbcKeys.PRIMARY_KEY)
                }
            }?.let { true }
        ?: false

    private fun selectRow(table: String, id: Value, rse: RowMapper<Record>) =
        jdbcTemplate.queryForObject(
            "SELECT * FROM $table WHERE $primaryKey = ?",
            rse,
            id.toAny()
        )

    private fun ResultSet.toRecord() =
        Record().also { map ->
            for (i in 1..metaData.columnCount) {
                map.setValue(metaData.getColumnName(i), getObject(i).toValue())
            }
        }
}
