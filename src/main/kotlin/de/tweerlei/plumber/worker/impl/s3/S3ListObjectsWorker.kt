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
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.GeneratingWorker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.InstantValue
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.Range
import de.tweerlei.plumber.worker.types.StringValue
import mu.KLogging

class S3ListObjectsWorker(
    private val bucketName: WorkItemAccessor<String>,
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
        val actualBucketName = StringValue.of(bucketName(item))
        val range = (item.getOptional(WellKnownKeys.RANGE) ?: Range()).toRange()
        val startAfter = range.startAfter.asOptional()?.toString()
        val endWith = range.endWith.asOptional()?.toString()
        logger.info { "fetching filenames from $startAfter to $endWith" }

        var result: ListObjectsV2Result? = null
        var firstKey: String? = null
        var lastKey: String? = null
        var itemCount = 0
        do {
            result = listFilenames(actualBucketName.toAny(), startAfter, result?.nextContinuationToken)
            logger.debug { "fetched ${result.objectSummaries.size} items" }
            result.objectSummaries.forEach { objectSummary ->
                if (endWith == null || objectSummary.key <= endWith) {
                    if (fn(objectSummary.toWorkItem(actualBucketName))) {
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

    private fun listFilenames(bucket: String, startWith: String?, continueWith: String?) =
        ListObjectsV2Request()
            .withBucketName(bucket)
            .withMaxKeys(numberOfFilesPerRequest.coerceAtMost(MAX_NUMBER_OF_KEYS))
            .withRequesterPays(requesterPays)
            .apply {
                if (continueWith != null)
                    withContinuationToken(continueWith)
                else
                    withStartAfter(startWith)
            }.let { request -> amazonS3Client.listObjectsV2(request) }

    private fun S3ObjectSummary.toWorkItem(bucket: StringValue) =
        StringValue.of(key)
            .let { keyValue ->
                WorkItem.of(
                    keyValue,
                    S3Keys.BUCKET_NAME to bucket,
                    S3Keys.OBJECT_KEY to keyValue,
                    WellKnownKeys.NAME to keyValue,
                    WellKnownKeys.SIZE to LongValue.of(size),
                    WellKnownKeys.LAST_MODIFIED to InstantValue.of(lastModified)
                )
            }
}
