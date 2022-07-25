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
import de.tweerlei.plumber.worker.*
import java.io.ByteArrayInputStream

class S3PutObjectWorker(
    private val bucketName: String,
    private val requesterPays: Boolean,
    private val amazonS3Client: AmazonS3,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getOptional(WellKnownKeys.NAME).coerceToString()
            .let { name ->
                putFile(
                    bucketName.ifEmptyGetFrom(item, S3Keys.BUCKET_NAME),
                    name,
                    item.getOptional().coerceToByteArray()
                )
            }.let { true }

    private fun putFile(bucket: String, fileName: String, contents: ByteArray) =
        ObjectMetadata()
            .apply { contentLength = contents.size.toLong() }
            .let { metadata ->
                PutObjectRequest(bucket, fileName, ByteArrayInputStream(contents), metadata)
                    .apply { isRequesterPays = requesterPays }
                    .let { request -> amazonS3Client.putObject(request) }
            }
}
