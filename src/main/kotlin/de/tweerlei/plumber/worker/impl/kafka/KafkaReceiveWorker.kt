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
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.InstantValue
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.StringValue
import de.tweerlei.plumber.worker.types.toValue
import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration

class KafkaReceiveWorker(
    private val topicName: String,
    private val waitSeconds: Int,
    private val follow: Boolean,
    private val consumer: KafkaConsumer<ByteArray, ByteArray>,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    override fun onOpen() {
        consumer.subscribe(listOf(topicName))
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        logger.info { "waiting $waitSeconds seconds for next message in $topicName" }
        val actualTopicName = StringValue.of(topicName)
        var keepGenerating = true
        var itemCount = 0
        while (keepGenerating) {
            keepGenerating = follow
            consumer.poll(Duration.ofSeconds(waitSeconds.toLong()))
                .also { records -> logger.debug { "fetched ${records.count()} items" } }
                .forEach { record ->
                    record.toWorkItem(actualTopicName)
                        ?.also { newItem ->
                            if (fn(newItem)) {
                                itemCount++
                            } else {
                                keepGenerating = false
                            }
                        }
                    }
                }
        logger.info { "received $itemCount messages" }
        }

    private fun ConsumerRecord<ByteArray, ByteArray>.toWorkItem(name: StringValue) =
        value()?.let { value ->
            key().toValue().let { keyValue ->
                WorkItem.of(
                    value.toValue(),
                    KafkaKeys.TOPIC_NAME to name,
                    KafkaKeys.PARTITION to LongValue.of(partition()),
                    KafkaKeys.OFFSET to LongValue.of(offset()),
                    KafkaKeys.KEY to keyValue,
                    WellKnownKeys.NAME to keyValue,
                    WellKnownKeys.LAST_MODIFIED to InstantValue.ofEpochMilli(timestamp())
                )
            }
        }

    override fun onClose() {
        consumer.unsubscribe()
    }
}
