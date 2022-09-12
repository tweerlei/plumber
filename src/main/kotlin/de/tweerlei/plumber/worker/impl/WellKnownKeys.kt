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
package de.tweerlei.plumber.worker.impl

class WellKnownKeys {

    companion object {
        /** file path (String) */
        const val PATH = "path"
        /** file name (String) */
        const val NAME = "name"
        /** file size (Long) */
        const val SIZE = "size"
        /** file modification time (Instant) */
        const val LAST_MODIFIED = "lastModified"
        /** data content type */
        const val CONTENT_TYPE = "contentType"

        /** content digest (String) */
        const val DIGEST = "digest"
        /** digest algorithm used (String) */
        const val DIGEST_ALGORITHM = "digestAlgorithm"

        /** item count (Long) */
        const val COUNT = "count"
        /** sum of item sizes (Long) */
        const val SUM = "sum"

        /** last test result (Boolean) */
        const val TEST_RESULT = "testResult"

        /** default range (Range) */
        const val RANGE = "range"
        /** default record (Record) */
        const val RECORD = "record"
        /** default node (JsonNode) */
        const val NODE = "node"

        /** parallel processing (Long) */
        const val WORKER_INDEX = "workerIndex"

        /** bulk processing (List<WorkItem>) */
        const val WORK_ITEMS = "workItems"
    }
}
