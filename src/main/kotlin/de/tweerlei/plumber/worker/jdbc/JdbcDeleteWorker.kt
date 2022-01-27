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

class JdbcDeleteWorker(
    private val tableName: String,
    private val primaryKey: String,
    private val jdbcTemplate: JdbcTemplate,
    worker: Worker
): DelegatingWorker(worker) {

    private var deleter: Deleter? = null

    override fun doProcess(item: WorkItem) =
        item.getFirstAs<Record>(WellKnownKeys.RECORD)
            .let { map ->
                deleterFor(item).process(map, jdbcTemplate)
            }.let { true }

    private fun deleterFor(item: WorkItem) =
        when (val v = deleter) {
            null -> Deleter.from(getTableName(item), primaryKey)
                .also { deleter = it }
            else -> v
        }

    private fun getTableName(item: WorkItem) =
        tableName.ifEmpty { item.getString(JdbcKeys.TABLE_NAME) }

    private class Deleter(
        private val sql: String,
        private val primaryKey: String
    ) {
        companion object {
            fun from(tableName: String, primaryKey: String) =
                Deleter(
                    buildSql(tableName, primaryKey),
                    primaryKey
                )

            private fun buildSql(tableName: String, primaryKey: String) =
                "DELETE FROM $tableName WHERE $primaryKey = ?"
        }

        fun process(map: Record, jdbcTemplate: JdbcTemplate) =
            jdbcTemplate.update(sql, map[primaryKey])
    }
}
