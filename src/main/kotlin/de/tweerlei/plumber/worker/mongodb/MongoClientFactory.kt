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
package de.tweerlei.plumber.worker.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.client.MongoClients
import de.tweerlei.plumber.util.createSSLContextForCustomCA
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.FileInputStream

@Service
@ConfigurationProperties(prefix = "spring.data")
class MongoClientFactory {

    lateinit var mongodb: Map<String, String>

    fun createClient() =
        MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(mongodb.getValue("uri")))
            .credential(MongoCredential.createCredential(
                mongodb.getValue("username"),
                mongodb.getValue("authenticationDatabase"),
                mongodb.getValue("password").toCharArray())
            ).apply {
                createCustomSslContext()
                    ?.also { context ->
                        applyToSslSettings { builder ->
                            builder.context(context)
                        }
                    }
            }.build()
            .let { settings ->
                MongoClients.create(settings)
            }

    fun getDefaultDatabase() =
        mongodb.getValue("database")

    private fun createCustomSslContext() =
        mongodb["sslrootcert"]
            ?.let { path ->
                FileInputStream(path).use {
                    createSSLContextForCustomCA(it)
                }
            }
}
