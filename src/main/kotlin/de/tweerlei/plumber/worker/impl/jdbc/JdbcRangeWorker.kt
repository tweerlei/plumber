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
import de.tweerlei.plumber.worker.types.Range
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import mu.KLogging
import org.springframework.jdbc.core.JdbcTemplate

class JdbcRangeWorker(
    private val tableName: String,
    private val primaryKey: String,
    private val jdbcTemplate: JdbcTemplate,
    worker: Worker
): DelegatingWorker(worker) {

    companion object: KLogging()

    override fun doProcess(item: WorkItem): Boolean =
        jdbcTemplate.queryForMap(
            "SELECT MIN($primaryKey) AS min_value, MAX($primaryKey) AS max_value FROM $tableName"
        ).let { map ->
            val minValue = map["min_value"]
            val maxValue = map["max_value"]

            if (minValue is Number && maxValue is Number) {
                logger.info { "Key range for $primaryKey is $minValue .. $maxValue" }
                item.set(Range.from(minValue.toLong() - 1, maxValue.toLong()), WellKnownKeys.RANGE)
                item.set(tableName, JdbcKeys.TABLE_NAME)
                item.set(primaryKey, JdbcKeys.PRIMARY_KEY)
                true
            } else {
                logger.warn { "Key range for $primaryKey is empty" }
                false
            }
        }
}
