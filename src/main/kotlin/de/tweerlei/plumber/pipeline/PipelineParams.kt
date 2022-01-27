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
package de.tweerlei.plumber.pipeline

data class PipelineParams(
    val steps: List<Step>,
    val explain: Boolean,
    val requesterPays: Boolean,
    val startAfterKey: String?,
    val stopAfterKey: String?,
    val startAfterRangeKey: String?,
    val stopAfterRangeKey: String?,
    val keyChars: String?,
    val primaryKey: String,
    val partitionKey: String,
    val rangeKey: String?,
    val numberOfFilesPerRequest: Int,
    val maxFilesPerThread: Int,
    val queueSizePerThread: Int,
    val maxWaitTimeSeconds: Int,
    val elementName: String,
    val rootElementName: String,
    val prettyPrint: Boolean,
    val follow: Boolean,
    val reread: Boolean,
    val assumeRoleArn: String?
) {
    data class Step(
        val action: String,
        val arg: String
    )
}
