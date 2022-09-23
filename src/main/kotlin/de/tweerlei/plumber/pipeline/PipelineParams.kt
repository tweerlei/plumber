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

import de.tweerlei.plumber.worker.types.ComparableValue

data class PipelineParams(
    val explain: Boolean,
    val requesterPays: Boolean,
    val startAfterKey: ComparableValue,
    val stopAfterKey: ComparableValue,
    val startAfterRangeKey: ComparableValue,
    val stopAfterRangeKey: ComparableValue,
    val keyChars: String,
    val primaryKey: String,
    val partitionKey: String,
    val rangeKey: String,
    val selectFields: Set<String>,
    val numberOfFilesPerRequest: Int,
    val maxFilesPerThread: Long,
    val queueSizePerThread: Int,
    val retryDelaySeconds: Int,
    val maxWaitTimeSeconds: Int,
    val elementName: String,
    val rootElementName: String,
    val wrapRoot: Boolean,
    val separator: Char,
    val header: Boolean,
    val prettyPrint: Boolean,
    val follow: Boolean,
    val reread: Boolean,
    val recursive: Boolean,
    val failFast: Boolean,
    val assumeRoleArn: String?
)
