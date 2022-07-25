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
package de.tweerlei.plumber.worker

class WellKnownKeys {

    companion object {
        // common metadata
        const val NAME = "name"
        const val SIZE = "size"
        const val LAST_MODIFIED = "lastModified"
        const val DIGEST = "digest"
        const val DIGEST_ALGORITHM = "digestAlgorithm"

        // aggregate values
        const val COUNT = "count"
        const val SUM = "sum"

        // records
        const val RANGE = "range"
        const val RECORD = "record"
        const val NODE = "node"

        // parallel processing
        const val WORKER_INDEX = "workerIndex"

        // bulk processing
        const val WORK_ITEMS = "workItems"
    }
}
