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

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
@ConfigurationProperties(prefix = "plumber.jdbc")
class JdbcTemplateFactory {

    lateinit var datasource: Map<String, String>

    fun createJdbcTemplate(poolSize: Int) =
        buildDataSource(HikariDataSource::class.java)
            .apply {
                maximumPoolSize = poolSize
            }.let {
                JdbcTemplate(it)
            }

    @Suppress("UNCHECKED_CAST")
    private fun <T: DataSource> buildDataSource(type: Class<T>) =
        DataSourceBuilder.create()
            .apply {
                type(type)
                driverClassName(datasource["driverClassName"])
                url(datasource["url"])
                username(datasource["username"])
                password(datasource["password"])
            }.build() as T
}
