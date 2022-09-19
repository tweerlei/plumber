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
import com.amazonaws.services.s3.model.GetObjectRequest
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom

class S3GetObjectWorker(
    private val bucketName: String,
    private val requesterPays: Boolean,
    private val amazonS3Client: AmazonS3,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getFirst(WellKnownKeys.NAME).toString()
            .let { name ->
                getFile(
                    bucketName.ifEmptyGetFrom(item, S3Keys.BUCKET_NAME),
                    name
                )
            }.also { file ->
                item.set(bucketName, S3Keys.BUCKET_NAME)
                item.set(file.key, S3Keys.OBJECT_KEY)
                item.set(file.objectMetadata.contentLength, WellKnownKeys.SIZE)
                file.objectMetadata.lastModified?.also { lastModified ->
                    item.set(lastModified.toInstant(), WellKnownKeys.LAST_MODIFIED)
                }
                file.objectContent.use { stream ->
                    stream.readAllBytes()
                }.also { bytes ->
                    item.set(bytes)
                }
            }.let { true }

    private fun getFile(bucket: String, fileName: String) =
        GetObjectRequest(bucket, fileName, requesterPays)
            .let { request -> amazonS3Client.getObject(request) }
}
