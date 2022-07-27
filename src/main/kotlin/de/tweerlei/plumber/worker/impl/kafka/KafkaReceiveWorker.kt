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

import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.Worker
import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.time.Instant

class KafkaReceiveWorker(
    private val topicName: String,
    private val waitSeconds: Int,
    private val follow: Boolean,
    private val consumer: KafkaConsumer<String, String>,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    override fun onOpen() {
        consumer.subscribe(listOf(topicName))
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        logger.info { "waiting $waitSeconds seconds for next message in $topicName" }
        var keepGenerating = true
        var itemCount = 0
        while (keepGenerating) {
            keepGenerating = follow
            // poll() with a zero timeout won't return any records
            consumer.poll(Duration.ofSeconds(waitSeconds.coerceAtLeast(1).toLong()))
                .also { records -> logger.debug { "fetched ${records.count()} items" } }
                .forEach { record ->
                    record.toWorkItem()
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

    private fun ConsumerRecord<String, String>.toWorkItem() =
        value()?.let { value ->
            WorkItem.of(value,
                KafkaKeys.TOPIC_NAME to topicName,
                KafkaKeys.PARTITION to partition(),
                KafkaKeys.OFFSET to offset(),
                KafkaKeys.KEY to key(),
                WellKnownKeys.NAME to key(),
                WellKnownKeys.LAST_MODIFIED to Instant.ofEpochMilli(timestamp())
            )
        }

    override fun onClose() {
        consumer.unsubscribe()
    }
}
