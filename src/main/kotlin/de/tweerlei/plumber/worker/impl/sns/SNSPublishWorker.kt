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
package de.tweerlei.plumber.worker.impl.sns

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.Value

class SNSPublishWorker(
    private val topicArn: WorkItemAccessor<Value>,
    private val amazonSNSClient: AmazonSNS,
    worker: Worker
): DelegatingWorker(worker) {

    companion object {
        // AWS limit for subject is 100 characters
        const val MAX_SUBJECT_LENGTH = 100
    }

    override fun doProcess(item: WorkItem) =
        item.get().toString()
            .let { body ->
                sendFile(
                    topicArn(item).toString(),
                    body,
                    item.get(WellKnownKeys.NAME).toString().take(MAX_SUBJECT_LENGTH)
                )
            }.let { true }

    private fun sendFile(url: String, body: String, subject: String) =
        PublishRequest(url, body, subject)
            .let { request -> amazonSNSClient.publish(request) }
}
