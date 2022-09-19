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
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.S3ObjectSummary
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.Range
import mu.KLogging

class S3ListObjectsWorker(
    private val bucketName: String,
    private val requesterPays: Boolean,
    private val numberOfFilesPerRequest: Int,
    private val amazonS3Client: AmazonS3,
    limit: Long,
    worker: Worker
): GeneratingWorker(limit, worker) {

    companion object : KLogging() {
        // AWS limit for maxKeys is 1000
        const val MAX_NUMBER_OF_KEYS = 1000
    }

    override fun generateItems(item: WorkItem, fn: (WorkItem) -> Boolean) {
        val range = item.getOptionalAs<Range>(WellKnownKeys.RANGE)
        val startAfter = range?.startAfter?.toStringOrNull()
        val endWith = range?.endWith?.toStringOrNull()
        logger.info { "fetching filenames from $startAfter to $endWith" }

        var result: ListObjectsV2Result? = null
        var firstKey: String? = null
        var lastKey: String? = null
        var itemCount = 0
        do {
            result = listFilenames(startAfter, result?.nextContinuationToken)
            logger.debug { "fetched ${result.objectSummaries.size} items" }
            result.objectSummaries.forEach { objectSummary ->
                if (endWith == null || objectSummary.key <= endWith) {
                    if (fn(objectSummary.toWorkItem())) {
                        itemCount++
                        if (firstKey == null) {
                            firstKey = objectSummary.key
                            logger.info { "first key: $firstKey" }
                        }
                        lastKey = objectSummary.key
                    } else {
                        result.nextContinuationToken = null
                    }
                } else {
                    result.nextContinuationToken = null
                }
            }
        } while (result?.nextContinuationToken != null)

        logger.info { "fetched $itemCount filenames from $startAfter to $endWith, first key: $firstKey, last key: $lastKey" }
    }

    private fun listFilenames(startWith: String?, continueWith: String?) =
        ListObjectsV2Request()
            .withBucketName(bucketName)
            .withMaxKeys(numberOfFilesPerRequest.coerceAtMost(MAX_NUMBER_OF_KEYS))
            .withRequesterPays(requesterPays)
            .apply {
                if (continueWith != null)
                    withContinuationToken(continueWith)
                else
                    withStartAfter(startWith)
            }.let { request -> amazonS3Client.listObjectsV2(request) }

    private fun S3ObjectSummary.toWorkItem() =
        WorkItem.from(
            key,
            S3Keys.BUCKET_NAME to bucketName,
            S3Keys.OBJECT_KEY to key,
            WellKnownKeys.NAME to key,
            WellKnownKeys.SIZE to size,
            WellKnownKeys.LAST_MODIFIED to lastModified.toInstant()
        )
}
