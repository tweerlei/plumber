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
package de.tweerlei.plumber.pipeline.steps

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.worker.Worker

interface ProcessingStep {

    val group: String
    val name: String
    val description: String
    val help: String
    val options: String
    val example: String
    val argDescription: String
    val argInterpolated: Boolean

    fun requiredAttributesFor(arg: String): Set<String> =
        emptySet()
    fun producedAttributesFor(arg: String): Set<String> =
        emptySet()
    fun parallelDegreeFor(arg: String): Int? =
        null

    fun missingRequiredAttributesFor(availableAttributes: Set<String>, arg: String): Set<String>? =
        (requiredAttributesFor(arg)
                + (if (argInterpolated) arg.toRequiredAttributes() else emptySet())
                - availableAttributes)
            .ifEmpty { null }

    fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ): Worker
}
