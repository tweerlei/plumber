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

import de.tweerlei.plumber.pipeline.steps.ProcessingStepFactory
import de.tweerlei.plumber.pipeline.PipelineParams
import mu.KLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.stereotype.Service

@Service
class CommandLineProcessor(
    private val factory: ProcessingStepFactory
) {

    companion object : KLogging()

    private fun showHelp() {
        StringBuilder()
            .append("""


                Multithreaded data processor.
                A processing pipeline is built from command line arguments.

                Supported steps are:


            """.trimIndent())
            .apply {
                factory.processingStepDescriptions().forEach { (group, steps) ->
                    append(group).append("\n")
                    steps.forEach { (keyword, name) ->
                        append("  $keyword:<arg>".padEnd(30))
                        append(name)
                        append("\n")
                    }
                }
            }
            .append("""

                Supported global options and their defaults (if any) are:

                --help                        Show this help
                --log-level=INFO              Set the log level
                --profile=default             Use 'quiet' to disable start-up banner and log only warnings and errors

                --explain                     Explain resulting plan, don't execute
                --fail-fast                   Fail on first processing error
                --limit=<n>                   Stop after reading n objects (per thread, default is unlimited)
                --bulk-size=1000              Bulk size for steps that process multiple items at once
                --queue-size=10               Queue size for items passed between threads
                --retry-delay=0               Wait this number of seconds before retrying failed messages
                --requester-pays              Requester pays access to S3 buckets
                --assume-role=<arn>           Assume the given IAM role for all AWS operations
                --start-after=<key>           Start after the given key
                --stop-after=<key>            Stop after the given key
                --start-range=<key>           Start after the given range key
                --stop-range=<key>            Stop after the given range key
                --key-chars=<list>            Use the given characters to generate keys (defaults to safe S3 chars)
                --primary-key=id              Use the given attribute as primary key (defaults to '_id' for MongoDB)
                --partition-key=<name>        Use the given attribute as DynamoDB partition key
                --range-key=<name>            Use the given attribute as DynamoDB range key
                --select=<fields>             Database fields to fetch, separated by commas
                --element-name=<name>         XML element name to read/write
                --root-element-name=<name>    XML root element name to wrap output in
                --separator=,                 CSV separator character
                --header                      Read/write CSV header
                --pretty-print                Pretty print JSON and XML output
                --wait=1                      Wait at most this number of seconds for a new message
                --follow                      Keep polling for new messages
                --reread                      Re-read all messages

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

    fun parseArguments(args: ApplicationArguments) =
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
                selectFields = args.getOptionValue("select")?.split(',')?.toSet() ?: emptySet(),
                numberOfFilesPerRequest = args.getOptionValue("bulk-size")?.toInt() ?: 1000,
                queueSizePerThread = args.getOptionValue("queue-size")?.toInt() ?: 10,
                maxFilesPerThread = args.getOptionValue("limit")?.toLong() ?: Long.MAX_VALUE,
                retryDelaySeconds = args.getOptionValue("retry-delay")?.toInt() ?: 0,
                maxWaitTimeSeconds = args.getOptionValue("wait")?.toInt() ?: 1,
                elementName = args.getOptionValue("element-name") ?: "",
                rootElementName = args.getOptionValue("root-element-name") ?: "",
                separator = args.getOptionValue("separator")?.first() ?: ',',
                header = args.containsOption("header"),
                prettyPrint = args.containsOption("pretty-print"),
                follow = args.containsOption("follow"),
                reread = args.containsOption("reread"),
                failFast = args.containsOption("fail-fast"),
                assumeRoleArn = args.getOptionValue("assume-role"),
                steps = parseSteps(args.nonOptionArgs)
            ).apply {
                if (explain) {
                    logger.info("requesting $numberOfFilesPerRequest items at once")
                    logger.info("stopping after $maxFilesPerThread items")
                    logger.info("${if (failFast) "skipping over" else "failing on"} processing errors")
                    logger.info("delaying retries for $retryDelaySeconds seconds")
                    logger.info("using queue size of $queueSizePerThread items per thread")
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
                    logger.info("XML: naming elements <$elementName> with root <$rootElementName>")
                }
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
}
