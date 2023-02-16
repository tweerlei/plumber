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
import com.amazonaws.services.sns.model.PublishBatchRequest
import com.amazonaws.services.sns.model.PublishBatchRequestEntry
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.WorkItemList
import de.tweerlei.plumber.worker.types.ifTypeIs

class SNSPublishBatchWorker(
    private val topicArn: String,
    private val amazonSNSClient: AmazonSNS,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.get(WellKnownKeys.WORK_ITEMS)
            .ifTypeIs { items: WorkItemList ->
                sendFiles(
                    topicArn,
                    items.toAny().mapIndexed { index, it ->
                        PublishBatchRequestEntry()
                            .withId("${index}_of_${items.size()}")
                            .withSubject(it.get(WellKnownKeys.NAME).toString())
                            .withMessage(it.get().toString())
                    }
                )
            }.let { true }

    private fun sendFiles(arn: String, bodies: List<PublishBatchRequestEntry>) =
        PublishBatchRequest().withTopicArn(arn).withPublishBatchRequestEntries(bodies)
            .let { request -> amazonSNSClient.publishBatch(request) }
}
