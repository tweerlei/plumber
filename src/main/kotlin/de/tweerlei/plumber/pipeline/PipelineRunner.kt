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

import de.tweerlei.plumber.util.humanReadable
import de.tweerlei.plumber.worker.WorkerBuilder
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.*
import mu.KLogging
import java.time.Duration

class PipelineRunner(
    private val factory: ProcessingStepFactory
) {

    companion object: KLogging()

    fun run(params: PipelineParams) {
        createWorkerDefinitions(params)
            .let { definitions -> createWorkers(definitions, params) }
            .let { worker -> if (!params.explain) runWorker(worker) }
    }

    private fun createWorkerDefinitions(params: PipelineParams): List<WorkerDefinition> {
        var parallelDegree = 1
        var producedAttributes = emptySet<String>()
        return params.steps.map { step ->
            factory.processingStepFor(step.action)
                .let { workerFactory ->
                    parallelDegree = workerFactory.parallelDegreeFor(step.arg) ?: parallelDegree
                    producedAttributes = producedAttributes.plus(workerFactory.producedAttributesFor(step.arg))
                    WorkerDefinition(
                        step.action,
                        step.arg,
                        workerFactory,
                        producedAttributes,
                        parallelDegree
                    )
                }
        }
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
                        if (index == 0) "None" else workers[index - 1].factory.name,
                        workers.getOrNull(index + 1),
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
        predecessorName: String,
        nextWorker: WorkerDefinition?,
        expectedOutput: Class<*>,
        params: PipelineParams
    ): WorkerBuilder {
        if (params.explain) {
            logger.info("Step ${String.format("%2d", index)}:${String.format("%4d", currentWorker.parallelDegree)}x ${currentWorker.factory.name}: ${currentWorker.arg}")
            logger.info("              Produces output: ${expectedOutput.simpleName} ${currentWorker.factory.producedAttributesFor(currentWorker.arg)}")
            if (nextWorker != null)
                logger.info("              Required output: ${nextWorker.factory.expectedInputFor(nextWorker.arg).simpleName} ${nextWorker.factory.requiredAttributesFor(nextWorker.arg)}")
        }

        if (nextWorker?.factory?.canConnectFrom(currentWorker.producedAttributes, nextWorker.arg) == false) {
            throw IllegalArgumentException("${currentWorker.action} cannot connect to ${nextWorker.action}")
        }

        return builder.append { w ->
            currentWorker.factory.createWorker(
                currentWorker.arg,
                expectedOutput,
                w,
                predecessorName,
                params,
                currentWorker.parallelDegree
            )
        }
    }

    private fun runWorker(
        worker: Worker
    ) {
        val startTime = System.currentTimeMillis()
        worker.open().use {
            worker.process(WorkItem.of(null))
        }
        val endTime = System.currentTimeMillis()
        logger.info("----------------------------------------------------------------------")
        val duration = Duration.ofMillis(endTime - startTime)
        logger.info("Total processing time: ${duration.humanReadable()}")
    }

    private data class WorkerDefinition(
        val action: String,
        val arg: String,
        val factory: ProcessingStep,
        val producedAttributes: Set<String>,
        val parallelDegree: Int
    )
}
