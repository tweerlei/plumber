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
package de.tweerlei.plumber.cmdline

import de.tweerlei.plumber.pipeline.PipelineDefinition
import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.options.AllPipelineOptions
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.pipeline.steps.ProcessingStepFactory
import mu.KLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.stereotype.Service

@Service
class CommandLineProcessor(
    private val factory: ProcessingStepFactory
) {

    companion object : KLogging()

    fun parseArguments(args: ApplicationArguments) =
        CommandLine.from(args).let { cmdline ->
            InclusionResolver().resolve(cmdline)
        }.let { cmdline ->
            when (val stepName = cmdline.showHelpFor()) {
                null -> cmdline.toPipelineDefinition()
                    .apply {
                        if (params.explain) {
                            params.showConfig()
                        }
                    }
                else -> showHelp(stepName)
                    .let { null }
            }
        }

    private fun showHelp(stepName: String) {
        try {
            factory.processingStepFor(stepName)
                .also { step -> showStepHelp(stepName, step) }
        } catch (e: Exception) {
            showGlobalHelp()
        }
    }

    private fun showStepHelp(stepName: String, step: ProcessingStep) {
        StringBuilder()
            .append("\nNAME\n  ")
            .append(stepName).append(" - ").append(step.description).append("\n")
            .append("\nUSAGE\n  ")
            .append(stepName).append(":").append(step.argDescription).append("\n")
            .append("\nDESCRIPTION\n  ")
            .append(step.help.replace("\n", "\n  "))
            .append(if (step.argInterpolated) "\n  You can pass @name to reference an item attribute and :string to disable smart casting.\n" else "\n")
            .append("\nOPTIONS\n  ")
            .append(step.options.ifEmpty { "None." }.replace("\n", "\n  ")).append("\n")
            .append("\nEXAMPLES\n  ")
            .append(step.example.replace("\n", "\n  ")).append("\n")
            .append("\nREQUIRED INPUTS\n  ")
            .append(step.requiredAttributesFor("").joinToString(", ").ifEmpty { "<none>" }).append("\n")
            .append("\nOUTPUTS\n  ")
            .append(step.producedAttributesFor("").joinToString(", ").ifEmpty { "<none>" }).append("\n")
            .toString()
            .also { message -> logger.warn(message) }
    }

    private fun showGlobalHelp() {
        StringBuilder()
            .append("""


                Multithreaded data processor.
                A processing pipeline is built from command line arguments.

                Supported steps and default arguments (if any) are:


            """.trimIndent())
            .apply {
                factory.processingStepDescriptions().forEach { (group, steps) ->
                    append(group).append("\n")
                    steps.forEach { (keyword, name) ->
                        append("  $keyword".padEnd(30))
                        append(name)
                        append("\n")
                    }
                }
            }
            .append("""

                Supported global options and their defaults (if any) are:

                --help                        Show this help
                --help=step-name              Show help for a specific step
                --profile=default             Use 'quiet' to disable start-up banner and log only warnings and errors
                                              Use 'verbose' to increase log output
                                              Use 'debug' for full debug logging


            """.trimIndent())
            .apply {
                AllPipelineOptions.INSTANCE.optionDescriptions().forEach { (keyword, name) ->
                    append("--$keyword".padEnd(30))
                    append(name)
                    append("\n")
                }
            }
            .append("""

                Credentials can be passed via environment variables:

                AWS_*                                          Set AWS credentials
                PLUMBER_JDBC_DATASOURCE_DRIVERCLASSNAME        Set JDBC driver class name
                PLUMBER_JDBC_DATASOURCE_URL                    Set JDBC url
                PLUMBER_JDBC_DATASOURCE_USERNAME               Set JDBC user name
                PLUMBER_JDBC_DATASOURCE_PASSWORD               Set JDBC password
                PLUMBER_MONGODB_CLIENT_URI                     Set MongoDB uri
                PLUMBER_MONGODB_CLIENT_USERNAME                Set MongoDB user name
                PLUMBER_MONGODB_CLIENT_PASSWORD                Set MongoDB password
                PLUMBER_MONGODB_CLIENT_DATABASE                Set MongoDB database
                PLUMBER_MONGODB_CLIENT_AUTHENTICATIONDATABASE  Set MongoDB authentication database
                PLUMBER_MONGODB_CLIENT_SSLROOTCERT             Set MongoDB SSL CA certificate
                PLUMBER_KAFKA_CONSUMER_*                       Set Kafka consumer config
                PLUMBER_KAFKA_PRODUCER_*                       Set Kafka producer config

            """.trimIndent())
            .toString()
            .also { message -> logger.warn(message) }
    }

    private fun PipelineParams.showConfig() {
        logger.info("requesting $numberOfFilesPerRequest items at once")
        logger.info("stopping after $maxFilesPerThread items")
        logger.info("${if (failFast) "skipping over" else "failing on"} processing errors")
        logger.info("delaying retries for $retryDelaySeconds seconds")
        logger.info("using queue size of $queueSizePerThread items per thread")
        logger.info("Files: ${if (recursive) "will" else "won't"} recurse into subdirectories")
        logger.info("AWS: assuming role '$assumeRoleArn' for AWS access")
        logger.info("AWS: ${if (requesterPays) "will" else "won't"} pay for S3 requests")
        logger.info("JDBC: using primary key '$primaryKey'")
        logger.info("JDBC: selecting fields $selectFields")
        logger.info("Partitions: starting after '$startAfterKey' up to and including '$stopAfterKey'")
        logger.info("Partitions: using key chars '$keyChars'")
        logger.info("Kafka/SQS: waiting for up to $maxWaitTimeSeconds seconds for new items")
        logger.info("Kafka/SQS: ${if (reread) "will" else "won't"} re-read all existing items, ${if (follow) "will" else "won't"} keep polling for new items")
        logger.info("DynamoDB: using partition key '$partitionKey' and range key '$rangeKey'")
        logger.info("DynamoDB: starting range after '$startAfterRangeKey' up to and including '$stopAfterRangeKey'")
        logger.info("CSV: using separator '$separator', ${if (header) "will" else "won't"} read/write header row")
        logger.info("JSON/XML: ${if (prettyPrint) "pretty printing" else "not pretty printing"} output")
        logger.info("XML: naming elements <$elementName> with root <$rootElementName> ${if (wrapRoot) "wrapped" else "not wrapped"}")
    }

    private fun CommandLine.showHelpFor() =
        when (val stepName = options["help"]) {
            null -> if (steps.isEmpty()) "" else null
            else -> stepName
        }

    private fun CommandLine.toPipelineDefinition() =
        PipelineDefinition(
            steps = mapSteps(),
            params = mapParams()
        )

    private fun CommandLine.mapSteps() =
        steps.map { step ->
            PipelineDefinition.Step(step.first, step.second)
        }

    private fun CommandLine.mapParams() =
        AllPipelineOptions.INSTANCE.parse { name ->
            options[name]
        }
}
