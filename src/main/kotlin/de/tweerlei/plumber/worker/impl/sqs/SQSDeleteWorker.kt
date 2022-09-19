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
package de.tweerlei.plumber.worker.impl.sqs

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom

class SQSDeleteWorker(
    private val queueUrl: String,
    private val amazonSQSClient: AmazonSQS,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.get(SQSKeys.DELETE_HANDLE).toString()
            .let { handle ->
                deleteFile(
                    queueUrl.ifEmptyGetFrom(item, SQSKeys.QUEUE_URL),
                    handle
                )
            }.let { true }

    private fun deleteFile(url: String, handle: String) =
        DeleteMessageRequest(url, handle)
            .let { request -> amazonSQSClient.deleteMessage(request) }
}
