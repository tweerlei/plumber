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

import de.tweerlei.plumber.pipeline.ProcessingStepFactory
import de.tweerlei.plumber.pipeline.PipelineRunner
import de.tweerlei.plumber.pipeline.PipelineParams
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

@SpringBootApplication(exclude = [
    JacksonAutoConfiguration::class,
    DataSourceAutoConfiguration::class,
    SqlInitializationAutoConfiguration::class,
    KafkaAutoConfiguration::class,
    TaskExecutionAutoConfiguration::class,
    TaskSchedulingAutoConfiguration::class
])
class PlumberApplication(
    private val factory: ProcessingStepFactory
) : ApplicationRunner {

    companion object : KLogging()

    private fun showHelp() {
        StringBuilder()
            .append("""


                Multithreaded data processor.
                A processing pipeline is built from command line arguments.
                
                Supported steps are:
                
                
            """.trimIndent())
            .apply {
                factory.processingStepDescriptions().forEach { (keyword, name) ->
                    append("$keyword:<arg>".padEnd(30))
                    append(name)
                    append("\n")
                }
            }
            .append("""
                
                Supported global options are:
                
                --help                        Show this help
                --explain                     Explain resulting plan, don't execute
                --requester-pays              Requester pays access to S3 buckets
                --assume-role=<arn>           Assume the given IAM role for all S3 operations
                --start-after=<key>           Start after the given key
                --stop-after=<key>            Stop after the given key
                --start-range=<key>           Start after the given range key
                --stop-range=<key>            Stop after the given range key
                --key-chars=<list>            Use the given list of characters to generate S3 partition keys
                --primary-key=<name>          Use the given JDBC column as primary key
                --partition-key=<name>        Use the given DynamoDB attribute as partition key
                --range-key=<name>            Use the given DynamoDB attribute as range key
                --element-name=<name>         Set XML element name to read/write
                --root-element-name=<name>    Set XML root element name to wrap output in
                --limit=<n>                   Stop after reading n objects (per thread)
                --queue-size=<n>              Queue size for items passed between threads
                --fetch-size=<n>              Fetch at most this number of items per request
                --wait=<n>                    Wait at most this number of seconds for a new message
                --follow                      Keep polling for new messages
                --reread                      Re-read all messages
            """.trimIndent())
            .toString()
            .also { message -> logger.info(message) }
    }

    private fun parseArguments(args: ApplicationArguments) =
        if (args.containsOption("help") || args.nonOptionArgs.isEmpty()) {
            showHelp()
            null
        } else {
            PipelineParams(
                explain = args.containsOption("explain"),
                requesterPays = args.containsOption("requester-pays"),
                startAfterKey = args.getOptionValue("start-after"),
                stopAfterKey = args.getOptionValue("stop-after"),
                startAfterRangeKey = args.getOptionValue("start-range"),
                stopAfterRangeKey = args.getOptionValue("stop-range"),
                keyChars = args.getOptionValue("key-chars"),
                primaryKey = args.getOptionValue("primary-key") ?: "",
                partitionKey = args.getOptionValue("partition-key") ?: "",
                rangeKey = args.getOptionValue("range-key"),
                numberOfFilesPerRequest = args.getOptionValue("fetch-size")?.toInt() ?: 1000,
                queueSizePerThread = args.getOptionValue("queue-size")?.toInt() ?: 10,
                maxFilesPerThread = args.getOptionValue("limit")?.toInt() ?: Int.MAX_VALUE,
                maxWaitTimeSeconds = args.getOptionValue("wait")?.toInt() ?: 0,
                elementName = args.getOptionValue("element-name") ?: "",
                rootElementName = args.getOptionValue("root-element-name") ?: "",
                follow = args.containsOption("follow"),
                reread = args.containsOption("reread"),
                assumeRoleArn = args.getOptionValue("assume-role"),
                steps = parseSteps(args.nonOptionArgs)
            ).apply {
                logger.info("----------------------------------------------------------------------")
                logger.info("${if (requesterPays) "paying" else "not paying"} for requests")
                logger.info("starting after $startAfterKey up to and including $stopAfterKey")
                logger.info("starting range after $startAfterRangeKey up to and including $stopAfterRangeKey")
                logger.info("generating partitions using key chars $keyChars")
                logger.info("requesting $numberOfFilesPerRequest file names at once")
                logger.info("waiting for up to $maxWaitTimeSeconds seconds for new items")
                logger.info("${if (reread) "will" else "won't"} re-read all existing items")
                logger.info("${if (follow) "will" else "won't"} keep polling for new items")
                logger.info("stopping after $maxFilesPerThread file names")
                logger.info("using queue size of $queueSizePerThread items per thread")
                logger.info("assuming role $assumeRoleArn for AWS access")
                logger.info("using JDBC primary key $primaryKey")
                logger.info("using DynamoDB partition key $partitionKey and range key $rangeKey")
                logger.info("naming XML elements $elementName with root $rootElementName")
                logger.info("----------------------------------------------------------------------")
            }
        }

    private fun ApplicationArguments.getOptionValue(name: String) =
        getOptionValues(name)?.singleOrNull()

    private fun parseSteps(steps: List<String>) =
        steps.map { step ->
            step.split(":", ignoreCase = false, limit = 2)
                .let { parts ->
                    when (parts.size) {
                        1 -> PipelineParams.Step(parts[0], "")
                        else -> PipelineParams.Step(parts[0], parts[1])
                    }
                }
        }

    override fun run(args: ApplicationArguments) {
        parseArguments(args)
            ?.let { params ->
                PipelineRunner(factory).run(params)
            }
    }
}

fun main(args: Array<String>) {
    runApplication<PlumberApplication>(*args)
}
