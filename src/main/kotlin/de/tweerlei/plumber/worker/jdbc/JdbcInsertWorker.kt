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

class JdbcInsertWorker(
    private val tableName: String,
    private val jdbcTemplate: JdbcTemplate,
    worker: Worker
): DelegatingWorker(worker) {

    private var updater: Updater? = null

    override fun doProcess(item: WorkItem) =
        item.getFirstAs<Record>(WellKnownKeys.RECORD)
            .let { map ->
                updaterFor(item, map).process(map, jdbcTemplate)
            }.let { true }

    private fun updaterFor(item: WorkItem, map: Record) =
        when (val v = updater) {
            null -> Updater.from(
                    item.getIfEmpty(tableName, JdbcKeys.TABLE_NAME),
                    map
                ).also { updater = it }
            else -> v
        }

    private class Updater(
        private val sql: String,
        private val columns: List<String>
    ) {
        companion object {
            fun from(tableName: String, map: Record) =
                Updater(
                    buildSql(tableName, map),
                    buildColumns(map)
                )

            private fun buildSql(tableName: String, map: Record) =
                StringBuilder().apply {
                    append("INSERT INTO $tableName (")
                    append(map.keys.joinToString(", "))
                    append(") VALUES (")
                    append(map.keys.joinToString(", ") { "?" })
                    append(")")
                }.toString()

            private fun buildColumns(map: Record) =
                map.keys.toList()
        }

        fun process(map: Record, jdbcTemplate: JdbcTemplate) =
            arrayOfNulls<Any>(columns.size).also { arr ->
                columns.forEachIndexed { index, columnName ->
                    arr[index] = map[columnName]
                }
            }.let { values ->
                jdbcTemplate.update(sql, *values)
            }
    }
}
