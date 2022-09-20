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

import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.ProcessingStepFactory
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.WorkerBuilder
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.NullValue
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class PipelineBuilder(
    private val factory: ProcessingStepFactory
) {

    companion object: KLogging() {
        private const val RANGE_RESET_STEP = "range-reset"
        private const val REPEAT_STEP = "repeat"
        private const val PARALLEL_STEP = "parallel"

        private val VIRTUAL_START_WORKER = WorkerDefinition(
            "<no predecessor>",
            "",
            object : ProcessingStep {
                override val group = "No group"
                override val name = "Nothing"
                override val description = "Start of pipeline"
                override fun createWorker(
                    arg: String,
                    expectedOutput: Class<*>,
                    w: Worker,
                    predecessorName: String,
                    params: PipelineParams,
                    parallelDegree: Int
                ): Worker =
                    throw IllegalStateException("Virtual step")
            },
            emptySet(),
            1
        )
    }

    fun build(definition: PipelineDefinition) =
        createWorkerDefinitions(definition)
            .also { if (definition.params.explain) logger.warn("Pipeline that would be executed:") }
            .let { definitions -> createWorkers(definitions, definition.params) }
            .let { worker -> if (definition.params.explain) null else worker }

    private fun createWorkerDefinitions(definition: PipelineDefinition): List<WorkerDefinition> {
        var parallelDegree = 1
        var producedAttributes = emptySet<String>()
        val workerDefinitions = mutableListOf<WorkerDefinition>()
        // Set initial range
        if (definition.params.startAfterKey !is NullValue || definition.params.stopAfterKey !is NullValue) {
            factory.processingStepFor(RANGE_RESET_STEP).let { processingStep ->
                producedAttributes = producedAttributes.plus(processingStep.producedAttributesFor(WellKnownKeys.RANGE))
                workerDefinitions.add(
                    WorkerDefinition(
                        RANGE_RESET_STEP,
                        WellKnownKeys.RANGE,
                        processingStep,
                        producedAttributes,
                        parallelDegree
                    )
                )
            }
        }
        // Set initial range
        if (definition.params.startAfterRangeKey !is NullValue || definition.params.stopAfterRangeKey !is NullValue) {
            factory.processingStepFor(RANGE_RESET_STEP).let { processingStep ->
                producedAttributes = producedAttributes.plus(processingStep.producedAttributesFor(WellKnownKeys.SECONDARY_RANGE))
                workerDefinitions.add(
                    WorkerDefinition(
                        RANGE_RESET_STEP,
                        WellKnownKeys.SECONDARY_RANGE,
                        processingStep,
                        producedAttributes,
                        parallelDegree
                    )
                )
            }
        }
        definition.steps.forEach { step ->
            factory.processingStepFor(step.action)
                .let { processingStep ->
                    val newParallelDegree = processingStep.parallelDegreeFor(step.arg) ?: parallelDegree
                    if ((newParallelDegree > parallelDegree) && workerDefinitions.isEmpty()) {
                        // Trigger all parallel workers by multiplying the initial WorkItem
                        workerDefinitions.add(
                            WorkerDefinition(
                                REPEAT_STEP,
                                newParallelDegree.toString(),
                                factory.processingStepFor(REPEAT_STEP),
                                producedAttributes,
                                1
                            )
                        )
                    }
                    if (newParallelDegree < parallelDegree) {
                        // Automatically add a parallel step to serialize processing
                        workerDefinitions.add(
                            WorkerDefinition(
                                PARALLEL_STEP,
                                newParallelDegree.toString(),
                                factory.processingStepFor(PARALLEL_STEP),
                                producedAttributes,
                                newParallelDegree
                            )
                        )
                    }
                    parallelDegree = newParallelDegree
                    producedAttributes = producedAttributes.plus(processingStep.producedAttributesFor(step.arg))
                    workerDefinitions.add(
                        WorkerDefinition(
                            step.action,
                            step.arg,
                            processingStep,
                            producedAttributes,
                            parallelDegree
                        )
                    )
                }
        }
        return workerDefinitions
    }

    private fun createWorkers(
        workers: List<WorkerDefinition>,
        params: PipelineParams
    ) =
        WorkerBuilder.create()
            .let { builder ->
                workers.foldIndexed(builder) { index, b, step ->
                    createWorker(
                        index,
                        b,
                        step,
                        workers.getOrElse(index - 1) { VIRTUAL_START_WORKER },
                        getExpectedOutput(workers, index),
                        params
                    )
                }
            }.build()

    private fun getExpectedOutput(
        workers: List<WorkerDefinition>,
        startAt: Int
    ) =
        workers.subList(startAt + 1, workers.size)
            .firstOrNull { step ->
                !step.factory.isValuePassThrough()
            }?.let { step ->
                step.factory.expectedInputFor(step.arg)
            }?: Any::class.java

    private fun createWorker(
        index: Int,
        builder: WorkerBuilder,
        currentWorker: WorkerDefinition,
        previousWorker: WorkerDefinition,
        expectedOutput: Class<*>,
        params: PipelineParams
    ): WorkerBuilder {
        if (params.explain) {
            logger.warn("Step ${String.format("%2d", index)}:${String.format("%4d", currentWorker.parallelDegree)}x ${currentWorker.factory.name}: ${currentWorker.arg}")
            logger.warn("              Required input: ${currentWorker.factory.expectedInputFor(currentWorker.arg).simpleName} ${currentWorker.factory.requiredAttributesFor(currentWorker.arg)}")
            logger.warn("              Produces output: ${expectedOutput.simpleName} ${currentWorker.factory.producedAttributesFor(currentWorker.arg)}")
        }

        currentWorker.factory.missingRequiredAttributesFor(previousWorker.producedAttributes, currentWorker.arg)?.also { missing ->
            throw IllegalArgumentException("'${currentWorker.action}' cannot connect to '${previousWorker.action}' due to missing attributes $missing")
        }

        return builder.append { w ->
            currentWorker.factory.createWorker(
                currentWorker.arg,
                expectedOutput,
                w,
                previousWorker.factory.name,
                params,
                currentWorker.parallelDegree
            )
        }
    }

    private data class WorkerDefinition(
        val action: String,
        val arg: String,
        val factory: ProcessingStep,
        val producedAttributes: Set<String>,
        val parallelDegree: Int
    )
}
