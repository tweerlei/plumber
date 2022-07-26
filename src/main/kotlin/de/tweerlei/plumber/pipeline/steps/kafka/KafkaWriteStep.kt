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
package de.tweerlei.plumber.pipeline.steps.kafka

import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.kafka.KafkaClientFactory
import de.tweerlei.plumber.worker.impl.kafka.KafkaKeys
import de.tweerlei.plumber.worker.impl.kafka.KafkaSendWorker
import org.springframework.stereotype.Service

@Service("kafka-writeWorker")
class KafkaWriteStep(
    private val kafkaClientFactory: KafkaClientFactory
): ProcessingStep {

    override val group = "Apache Kafka"
    override val name = "Send Kafka message"
    override val description = "Send a message to the given Kafka topic"

    override fun isValuePassThrough() = true
    override fun producedAttributesFor(arg: String) = setOf(
//        KafkaKeys.KAFKA_KEY,
        KafkaKeys.PARTITION,
        KafkaKeys.OFFSET
    )

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        kafkaClientFactory.createProducer()
            .let { client ->
                KafkaSendWorker(
                    arg,
                    client,
                    w
                )
            }
}
