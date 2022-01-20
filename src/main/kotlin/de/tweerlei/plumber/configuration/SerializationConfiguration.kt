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
package de.tweerlei.plumber.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class SerializationConfiguration {

    @Bean
    @Primary
    fun objectMapper() =
        ObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

    @Bean
    fun xmlMapper() =
        XmlMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

    @Bean
    fun csvMapper() =
        CsvMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
}
