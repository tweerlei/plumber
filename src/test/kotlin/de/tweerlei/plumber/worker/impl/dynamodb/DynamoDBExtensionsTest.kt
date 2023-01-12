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
package de.tweerlei.plumber.worker.impl.dynamodb

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import de.tweerlei.plumber.worker.types.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.nio.ByteBuffer
import java.time.Instant

class DynamoDBExtensionsTest {

    @Test
    fun testFromDynamoDB() {
        val dynamoDBRecord = mapOf(
            "string" to AttributeValue().apply { s = "Hello, World!" },
            "number" to AttributeValue().apply { n = "42" },
            "bytes" to AttributeValue().apply { b = ByteBuffer.wrap(byteArrayOf(0x01, 0x02, 0x04, 0x09)) },
            "boolean" to AttributeValue().apply { bool = false },
            "set" to AttributeValue().apply {
                setSS(listOf("one", "two", "three"))
            },
            "list" to AttributeValue().apply {
                setL(listOf(
                    AttributeValue().apply { s = "Hello, World!" }
                ))
            },
            "map" to AttributeValue().apply {
                m = mapOf(
                    "one" to AttributeValue().apply { s = "Hello, World!" }
                )
            }
        )

        val record = dynamoDBRecord.fromDynamoDB(ObjectMapper())

        with (record.toAny()) {
            this["string"].shouldBe(StringValue.of("Hello, World!"))
            this["number"].shouldBe(BigDecimalValue.of(BigDecimal.valueOf(42L)))
            this["bytes"].shouldBe(ByteArrayValue.of(byteArrayOf(0x01, 0x02, 0x04, 0x09)))
            this["boolean"].shouldBe(BooleanValue.FALSE)
            this["set"].shouldBe(JsonNodeFactory.instance.arrayNode().apply {
                add("one")
                add("two")
                add("three")
            }.let { Node(it) })
            this["list"].shouldBe(JsonNodeFactory.instance.arrayNode().apply {
                add("Hello, World!")
            }.let { Node(it) })
            this["map"].shouldBe(JsonNodeFactory.instance.objectNode().apply {
                put("one", "Hello, World!")
            }.let { Node(it) })
        }
    }

    @Test
    fun testToDynamoDB() {
        val record = Record.of(
            "string" to StringValue.of("Hello, World!"),
            "number" to BigDecimalValue.of(BigDecimal.valueOf(42L)),
            "bytes" to ByteArrayValue.of(byteArrayOf(0x01, 0x02, 0x04, 0x09)),
            "boolean" to BooleanValue.FALSE,
            "set" to JsonNodeFactory.instance.arrayNode().apply {
                add("one")
                add("two")
                add("three")
            }.let { Node(it) },
            "list" to JsonNodeFactory.instance.arrayNode().apply {
                add("Hello, World!")
            }.let { Node(it) },
            "map" to JsonNodeFactory.instance.objectNode().apply {
                put("one", "Hello, World!")
            }.let { Node(it) },
            "instant" to InstantValue.of(Instant.ofEpochSecond(1673302586L))
        )

        val dynamoDBRecord = record.toDynamoDB(ObjectMapper())

        dynamoDBRecord["string"].shouldBe(AttributeValue().apply { s = "Hello, World!" })
        dynamoDBRecord["number"].shouldBe(AttributeValue().apply { n = "42" })
        dynamoDBRecord["bytes"].shouldBe(AttributeValue().apply { b = ByteBuffer.wrap(byteArrayOf(0x01, 0x02, 0x04, 0x09)) })
        dynamoDBRecord["boolean"].shouldBe(AttributeValue().apply { bool = false })
        dynamoDBRecord["set"].shouldBe(AttributeValue().apply {
            setL(listOf(
                AttributeValue().apply { s = "one" },
                AttributeValue().apply { s = "two" },
                AttributeValue().apply { s = "three" }
            ))
        })
        dynamoDBRecord["list"].shouldBe(AttributeValue().apply {
            setL(listOf(
                AttributeValue().apply { s = "Hello, World!" }
            ))
        })
        dynamoDBRecord["map"].shouldBe(AttributeValue().apply {
            m = mapOf(
                "one" to AttributeValue().apply { s = "Hello, World!" }
            )
        })
        dynamoDBRecord["instant"].shouldBe(AttributeValue().apply { n = "1673302586000" })
    }
}
