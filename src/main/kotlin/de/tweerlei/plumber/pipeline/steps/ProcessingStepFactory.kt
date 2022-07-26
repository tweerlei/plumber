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

import org.springframework.stereotype.Service
import java.util.TreeMap

@Service
class ProcessingStepFactory(
    private val processingSteps: Map<String, ProcessingStep>
) {

    companion object {
        const val BEAN_SUFFIX = "Worker"
    }

    fun processingStepFor(stepName: String) =
        processingSteps[stepNameToBeanName(stepName)] ?: throw IllegalArgumentException("Unknown worker type '$stepName'")

    fun processingStepDescriptions() =
        TreeMap<String, TreeMap<String, String>>().also { map ->
            processingSteps.entries.forEach { (key, value) ->
                beanNameToStepName(key).let { stepName ->
                    map.computeIfAbsent(value.group) { TreeMap<String, String>() }[stepName] = value.description
                }
            }
        }

    private fun stepNameToBeanName(stepName: String) =
        "$stepName$BEAN_SUFFIX"

    private fun beanNameToStepName(beanName: String) =
        beanName.substring(0, beanName.length - BEAN_SUFFIX.length)
}
