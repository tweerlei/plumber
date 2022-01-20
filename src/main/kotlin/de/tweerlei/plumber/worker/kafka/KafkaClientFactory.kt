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
package de.tweerlei.plumber.worker.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.util.*

@Service
@ConfigurationProperties(prefix = "spring.kafka")
class KafkaClientFactory {

    lateinit var producer: Map<String, String>
    lateinit var consumer: Map<String, String>

    fun createProducer() =
        producer.plus(mapOf(
            "key.serializer" to StringSerializer::class.java.name,
            "value.serializer" to StringSerializer::class.java.name,
            "enable.idempotence" to "true"
        )).let { props ->
            KafkaProducer<String, String>(props)
        }

    fun createConsumer(maxRecordsPerPoll: Int, reread: Boolean) =
        consumer.plus(mapOf(
            "key.deserializer" to StringDeserializer::class.java.name,
            "value.deserializer" to StringDeserializer::class.java.name,
            "group.id" to "plumber-${UUID.randomUUID()}",
            "max.poll.records" to maxRecordsPerPoll,
            "enable.auto.commit" to "false",
            "auto.offset.reset" to if (reread) "earliest" else "latest"
        )).let { props ->
            KafkaConsumer<String, String>(props)
        }
}
