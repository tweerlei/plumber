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
package de.tweerlei.plumber.worker.impl.kafka

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.LongValue
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

class KafkaSendWorker(
    private val topicName: WorkItemAccessor<String>,
    private val producer: KafkaProducer<ByteArray, ByteArray>,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.toProducerRecord()
            .let { record ->
                producer.send(record).get()
            }.also { metadata ->
                item.set(LongValue.of(metadata.partition()), KafkaKeys.PARTITION)
                item.set(LongValue.of(metadata.offset()), KafkaKeys.OFFSET)
            }.let { true }

    private fun WorkItem.toProducerRecord() =
        when (val name = getOptional(WellKnownKeys.NAME)) {
            null -> ProducerRecord<ByteArray, ByteArray>(topicName(this), get().toByteArray())
            else -> ProducerRecord<ByteArray, ByteArray>(topicName(this), name.toByteArray(), get().toByteArray())
        }
}
