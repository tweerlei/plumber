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
package de.tweerlei.plumber.worker.impl.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import de.tweerlei.plumber.worker.types.WorkItemList
import de.tweerlei.plumber.worker.types.ifTypeIs

class S3DeleteObjectsWorker(
    private val bucketName: String,
    private val requesterPays: Boolean,
    private val amazonS3Client: AmazonS3,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.get(WellKnownKeys.WORK_ITEMS)
            .ifTypeIs { items: WorkItemList ->
                deleteFiles(
                    bucketName.ifEmptyGetFrom(items.toAny().first(), S3Keys.BUCKET_NAME),
                    items.toAny().map { item ->
                        item.getFirst(WellKnownKeys.NAME).toString()
                            .let { DeleteObjectsRequest.KeyVersion(it) }
                    }
                )
            }.let { true }

    private fun deleteFiles(bucket: String, fileNames: List<DeleteObjectsRequest.KeyVersion>) =
        DeleteObjectsRequest(bucket)
            .apply {
                isRequesterPays = requesterPays
                keys = fileNames
            }.let { request -> amazonS3Client.deleteObjects(request) }
}
