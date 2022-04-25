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

import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.WorkerBuilder
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class PipelineBuilder(
    private val factory: ProcessingStepFactory
) {

    companion object: KLogging() {
        private val VIRTUAL_START_WORKER = WorkerDefinition(
            "<no predecessor>",
            "",
            object : ProcessingStep {
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

    fun build(params: PipelineParams) =
        createWorkerDefinitions(params)
            .let { definitions -> createWorkers(definitions, params) }
            .let { worker -> if (params.explain) null else worker }

    private fun createWorkerDefinitions(params: PipelineParams): List<WorkerDefinition> {
        var parallelDegree = 1
        var producedAttributes = emptySet<String>()
        val workerDefinitions = mutableListOf<WorkerDefinition>()
        params.steps.forEach { step ->
            factory.processingStepFor(step.action)
                .let { processingStep ->
                    val newParallelDegree = processingStep.parallelDegreeFor(step.arg) ?: parallelDegree
                    if (newParallelDegree < parallelDegree) {
                        // Automatically add a parallel step to serialize processing
                        workerDefinitions.add(
                            WorkerDefinition(
                                "parallel",
                                newParallelDegree.toString(),
                                factory.processingStepFor("parallel"),
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
            logger.info("Step ${String.format("%2d", index)}:${String.format("%4d", currentWorker.parallelDegree)}x ${currentWorker.factory.name}: ${currentWorker.arg}")
            logger.info("              Required input: ${currentWorker.factory.expectedInputFor(currentWorker.arg).simpleName} ${currentWorker.factory.requiredAttributesFor(currentWorker.arg)}")
            logger.info("              Produces output: ${expectedOutput.simpleName} ${currentWorker.factory.producedAttributesFor(currentWorker.arg)}")
        }

        if (!currentWorker.factory.canConnectFrom(previousWorker.producedAttributes, currentWorker.arg)) {
            throw IllegalArgumentException("${currentWorker.action} cannot connect to ${previousWorker.action}, check required input")
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
