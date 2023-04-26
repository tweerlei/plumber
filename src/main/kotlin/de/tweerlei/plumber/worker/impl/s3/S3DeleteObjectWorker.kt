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
import com.amazonaws.services.s3.model.DeleteObjectRequest
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom

class S3DeleteObjectWorker(
    private val bucketName: WorkItemAccessor<String>,
    private val requesterPays: Boolean,
    private val amazonS3Client: AmazonS3,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirst(WellKnownKeys.NAME).toString()
            .let { name ->
                deleteFile(
                    bucketName(item).ifEmptyGetFrom(item, S3Keys.BUCKET_NAME),
                    name
                )
            }.let { true }

    private fun deleteFile(bucket: String, fileName: String) =
        DeleteObjectRequest(bucket, fileName)
            .apply { isRequesterPays = requesterPays }
            .let { request -> amazonS3Client.deleteObject(request) }
}
