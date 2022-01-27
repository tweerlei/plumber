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
package de.tweerlei.plumber.worker.sqs

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.GeneratingWorker
import de.tweerlei.plumber.worker.Worker
import mu.KLogging
import kotlin.math.min

class SQSReceiveWorker(
    private val queueUrl: String,
    private val numberOfFilesPerRequest: Int,
    private val waitSeconds: Int,
    private val follow: Boolean,
    private val amazonSQSClient: AmazonSQS,
    limit: Int,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object: KLogging()

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        logger.info { "waiting $waitSeconds seconds for next message in $queueUrl" }
        var keepGenerating = true
        var itemCount = 0
        while (keepGenerating) {
            receiveFile()
                .also {
                    keepGenerating = follow || it.isNotEmpty()
                }.forEach { message ->
                    if (fn(message.toWorkItem())) {
                        itemCount++
                    } else {
                        keepGenerating = false
                    }
                }
        }
        logger.info { "received $itemCount messages" }
    }

    private fun receiveFile() =
        ReceiveMessageRequest(queueUrl)
            .withWaitTimeSeconds(min(waitSeconds, 20)) // AWS limit is 20
            .withMaxNumberOfMessages(min(numberOfFilesPerRequest, 10)) // AWS limit is 10
            .let { request -> amazonSQSClient.receiveMessage(request) }
            .messages

    private fun Message.toWorkItem() =
        WorkItem.of(
            body,
            WellKnownKeys.NAME to messageId,
            SQSKeys.QUEUE_URL to queueUrl,
            SQSKeys.MESSAGE_ID to messageId,
            SQSKeys.DELETE_HANDLE to receiptHandle
        )
}
