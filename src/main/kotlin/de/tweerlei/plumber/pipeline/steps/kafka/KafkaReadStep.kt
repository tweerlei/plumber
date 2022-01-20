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

import de.tweerlei.plumber.pipeline.ProcessingStep
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.kafka.KafkaClientFactory
import de.tweerlei.plumber.worker.kafka.KafkaKeys
import de.tweerlei.plumber.worker.kafka.KafkaReceiveWorker
import org.springframework.stereotype.Service

@Service("kafka-readWorker")
class KafkaReadStep(
    private val kafkaClientFactory: KafkaClientFactory
): ProcessingStep {

    override val name = "Receive Kafka messages"
    override val description = "Receive messages from the given Kafka topic"

    override fun producedAttributesFor(arg: String) = setOf(
//        WellKnownKeys.NAME,
//        KafkaKeys.KAFKA_KEY,
        KafkaKeys.TOPIC_NAME,
        KafkaKeys.PARTITION,
        KafkaKeys.OFFSET,
        WellKnownKeys.LAST_MODIFIED
    )

    override fun createWorker(
        arg: String,
        expectedOutput: Class<*>,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        kafkaClientFactory.createConsumer(params.numberOfFilesPerRequest, params.reread)
            .let { client ->
                KafkaReceiveWorker(
                    arg,
                    params.maxWaitTimeSeconds,
                    params.follow,
                    client,
                    params.maxFilesPerThread,
                    w
                )
            }
}
