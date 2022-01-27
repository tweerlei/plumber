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
package de.tweerlei.plumber

import de.tweerlei.plumber.pipeline.PipelineBuilder
import de.tweerlei.plumber.util.humanReadable
import de.tweerlei.plumber.util.printStackTraceUpTo
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import mu.KLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration
import org.springframework.boot.runApplication
import java.time.Duration

@SpringBootApplication(exclude = [
    JacksonAutoConfiguration::class,
    DataSourceAutoConfiguration::class,
    SqlInitializationAutoConfiguration::class,
    KafkaAutoConfiguration::class,
    TaskExecutionAutoConfiguration::class,
    TaskSchedulingAutoConfiguration::class
])
class PlumberApplication(
    private val cmdLineProcessor: CommandLineProcessor,
    private val pipelineBuilder: PipelineBuilder
) : ApplicationRunner {

    companion object : KLogging()

    override fun run(args: ApplicationArguments) {
        try {
            logger.info("_______________________________________________________Configuration__")
            cmdLineProcessor.parseArguments(args)
                ?.let { params ->
                    logger.info("___________________________________________________Building pipeline__")
                    pipelineBuilder.build(params)
                }?.let { worker ->
                    logger.info("____________________________________________________Running pipeline__")
                    runWorker(worker)
                }?.let { duration ->
                    logger.info("_____________________________________________________________Summary__")
                    logger.info("Total processing time: ${duration.humanReadable()}")
                }
        } catch (e: Exception) {
            logger.error {
                "Error while building the pipeline\n" +
                e.printStackTraceUpTo(PlumberApplication::class)
            }
        }
    }

    private fun runWorker(
        worker: Worker
    ): Duration {
        val startTime = System.currentTimeMillis()
        worker.open().use {
            worker.process(WorkItem.of(null))
        }
        val endTime = System.currentTimeMillis()
        return Duration.ofMillis(endTime - startTime)
    }
}

fun main(args: Array<String>) {
    runApplication<PlumberApplication>(*args)
}
