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
package de.tweerlei.plumber.worker.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import de.tweerlei.plumber.worker.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import java.io.ByteArrayInputStream

class S3PutObjectWorker(
    private val bucketName: String,
    private val requesterPays: Boolean,
    private val amazonS3Client: AmazonS3,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getString(WellKnownKeys.NAME)
            .let { name -> putFile(getBucketName(item), name, item.getByteArray()) }
            .let { true }

    private fun getBucketName(item: WorkItem) =
        bucketName.ifEmpty { item.getString(S3Keys.BUCKET_NAME) }

    private fun putFile(bucket: String, fileName: String, contents: ByteArray) =
        ObjectMetadata()
            .apply { contentLength = contents.size.toLong() }
            .let { metadata ->
                PutObjectRequest(bucket, fileName, ByteArrayInputStream(contents), metadata)
                    .apply { isRequesterPays = requesterPays }
                    .let { request -> amazonS3Client.putObject(request) }
            }
}
