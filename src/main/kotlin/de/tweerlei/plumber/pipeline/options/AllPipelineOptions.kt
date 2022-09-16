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
package de.tweerlei.plumber.pipeline.options

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.toOptionValue

data class AllPipelineOptions(
    val explain: PipelineOption<Boolean>,
    val numberOfFilesPerRequest: PipelineOption<Int>,
    val maxFilesPerThread: PipelineOption<Long>,
    val failFast: PipelineOption<Boolean>,
    val retryDelaySeconds: PipelineOption<Int>,
    val queueSizePerThread: PipelineOption<Int>,
    val primaryKey: PipelineOption<String>,
    val selectFields: PipelineOption<Set<String>>,
    val startAfterKey: PipelineOption<Comparable<*>?>,
    val stopAfterKey: PipelineOption<Comparable<*>?>,
    val keyChars: PipelineOption<String>,
    val assumeRoleArn: PipelineOption<String?>,
    val requesterPays: PipelineOption<Boolean>,
    val maxWaitTimeSeconds: PipelineOption<Int>,
    val follow: PipelineOption<Boolean>,
    val reread: PipelineOption<Boolean>,
    val partitionKey: PipelineOption<String>,
    val rangeKey: PipelineOption<String?>,
    val startAfterRangeKey: PipelineOption<Comparable<*>?>,
    val stopAfterRangeKey: PipelineOption<Comparable<*>?>,
    val separator: PipelineOption<Char>,
    val header: PipelineOption<Boolean>,
    val prettyPrint: PipelineOption<Boolean>,
    val elementName: PipelineOption<String>,
    val rootElementName: PipelineOption<String>
) {
    companion object {
        // Safe characters, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-keys.html
        const val SAFE_KEY_CHARS = "!'()*-./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"

        val INSTANCE = AllPipelineOptions(
            explain = BooleanPipelineOption(
                "explain",
                "Explain resulting plan, don't execute"
            ),
            failFast = BooleanPipelineOption(
                "fail-fast",
                "Fail on first processing error"
            ),
            maxFilesPerThread = CustomPipelineOption(
                "limit",
                "Stop after reading n objects (per thread, default is unlimited)"
            ) { value ->
                value?.toLong() ?: Long.MAX_VALUE
            },
            numberOfFilesPerRequest = IntPipelineOption(
                "bulk-size",
                "Bulk size for steps that process multiple items at once",
                1000
            ),
            queueSizePerThread = IntPipelineOption(
                "queue-size",
                "Queue size for items passed between threads",
                10
            ),
            retryDelaySeconds = IntPipelineOption(
                "retry-delay",
                "Wait this number of seconds before retrying failed messages",
                0
            ),
            requesterPays = BooleanPipelineOption(
                "requester-pays",
                "AWS: Requester pays access to S3 buckets"
            ),
            assumeRoleArn = DefaultPipelineOption(
                "assume-role",
                "AWS: Assume the given IAM role for all AWS operations"
            ),
            startAfterKey = CustomPipelineOption(
                "start-after",
                "Start after the given key",
                "<value>"
            ) { value -> value.toOptionValue() },
            stopAfterKey = CustomPipelineOption(
                "stop-after",
                "Stop after the given key",
                "<value>"
            ) { value -> value.toOptionValue() },
            startAfterRangeKey = CustomPipelineOption(
                "start-range",
                "DynamoDB: Start after the given range key",
                "<value>"
            ) { value -> value.toOptionValue() },
            stopAfterRangeKey = CustomPipelineOption(
                "stop-range",
                "DynamoDB: Stop after the given range key",
                "<value>"
            ) { value -> value.toOptionValue() },
            keyChars = CustomPipelineOption(
                "key-chars",
                "Use the given characters to generate keys (defaults to safe S3 chars)",
                "<arg>"
            ) { value -> value ?: SAFE_KEY_CHARS },
            primaryKey = StringPipelineOption(
                "primary-key",
                "Use the given attribute as primary key (defaults to 'id' for JDBC and '_id' for MongoDB)",
                ""
            ),
            partitionKey = StringPipelineOption(
                "partition-key",
                "DynamoDB: Use the given attribute as partition key",
                ""
            ),
            rangeKey = DefaultPipelineOption(
                "range-key",
                "DynamoDB: Use the given attribute as range key"
            ),
            selectFields = CustomPipelineOption(
                "select",
                "Database fields to fetch, separated by commas (defaults to selecting all fields)",
                "<arg>"
            ) { value ->
                value?.split(',')?.toSet() ?: emptySet()
            },
            elementName = StringPipelineOption(
                "element-name",
                "XML: Element name to read/write",
                "item"
            ),
            rootElementName = StringPipelineOption(
                "root-element-name",
                "XML: Root element name to wrap output in",
                "items"
            ),
            separator = CustomPipelineOption(
                "separator",
                "CSV: Separator character"
            ) { value ->
                value?.first() ?: ','
            },
            header = BooleanPipelineOption(
                "header",
                "CSV: Read/write header"
            ),
            prettyPrint = BooleanPipelineOption(
                "pretty-print",
                "Pretty print JSON and XML output"
            ),
            maxWaitTimeSeconds = IntPipelineOption(
                "wait",
                "Kafka/SQS: Wait at most this number of seconds for a new message",
                1
            ),
            follow = BooleanPipelineOption(
                "follow",
                "Kafka/SQS: Keep polling for new messages"
            ),
            reread = BooleanPipelineOption(
                "reread",
                "Kafka/SQS: Re-read all messages"
            ),
        )
    }

    fun optionDescriptions() =
        // Kotlin bug: declaredMemberProperties is not in declaration order
        AllPipelineOptions::class.java.declaredFields
            .asSequence()
            .filter { PipelineOption::class.java.isAssignableFrom(it.type) }
            .associate { m ->
                (m.get(this) as PipelineOption<*>).let { o ->
                    when (val d = o.argDescription()) {
                        "" -> o.name
                        else -> "${o.name}=$d"
                    } to o.description
                }
            }

    fun parse(accessor: (String) -> String?) =
        PipelineParams(
            explain = explain.readFrom(accessor),
            requesterPays = requesterPays.readFrom(accessor),
            startAfterKey = startAfterKey.readFrom(accessor),
            stopAfterKey = stopAfterKey.readFrom(accessor),
            startAfterRangeKey = startAfterRangeKey.readFrom(accessor),
            stopAfterRangeKey = stopAfterRangeKey.readFrom(accessor),
            keyChars = keyChars.readFrom(accessor),
            primaryKey = primaryKey.readFrom(accessor),
            partitionKey = partitionKey.readFrom(accessor),
            rangeKey = rangeKey.readFrom(accessor),
            selectFields = selectFields.readFrom(accessor),
            numberOfFilesPerRequest = numberOfFilesPerRequest.readFrom(accessor),
            queueSizePerThread = queueSizePerThread.readFrom(accessor),
            maxFilesPerThread = maxFilesPerThread.readFrom(accessor),
            retryDelaySeconds = retryDelaySeconds.readFrom(accessor),
            maxWaitTimeSeconds = maxWaitTimeSeconds.readFrom(accessor),
            elementName = elementName.readFrom(accessor),
            rootElementName = rootElementName.readFrom(accessor),
            separator = separator.readFrom(accessor),
            header = header.readFrom(accessor),
            prettyPrint = prettyPrint.readFrom(accessor),
            follow = follow.readFrom(accessor),
            reread = reread.readFrom(accessor),
            failFast = failFast.readFrom(accessor),
            assumeRoleArn = assumeRoleArn.readFrom(accessor)
        )

    private fun <T> PipelineOption<T>.readFrom(accessor: (String) -> String?) =
        accessor(name).let { value ->
            try {
                parse(value)
            } catch (e: RuntimeException) {
                throw IllegalArgumentException("Invalid value '$value' for option --$name")
            }
        }
}
