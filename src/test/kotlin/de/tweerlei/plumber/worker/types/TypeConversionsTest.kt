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
package de.tweerlei.plumber.worker.types

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.time.Instant

class TypeConversionsTest {

    @Test
    fun testCoerceToLong() {
        null.coerceToLong().shouldBe(0L)
        "Hallo".coerceToLong().shouldBe(0L)
        42.coerceToLong().shouldBe(42L)
        3.1415.coerceToLong().shouldBe(3L)
        Instant.ofEpochMilli(123456789012L).coerceToLong().shouldBe(123456789012L)
        "Hallo".toByteArray(StandardCharsets.UTF_8).coerceToLong().shouldBe(1819042120L)
        true.coerceToLong().shouldBe(1L)
        false.coerceToLong().shouldBe(0L)
        listOf<Instant>().coerceToLong().shouldBe(0L)
    }

    @Test
    fun testCoerceToNumber() {
        null.coerceToNumber().shouldBe(0L)
        "Hallo".coerceToNumber().shouldBe(0L)
        42.coerceToNumber().shouldBe(42L)
        3.1415.coerceToNumber().shouldBe(3.1415)
        Instant.ofEpochMilli(123456789012L).coerceToNumber().shouldBe(123456789012L)
        "Hallo".toByteArray(StandardCharsets.UTF_8).coerceToNumber().shouldBe(1819042120L)
        true.coerceToNumber().shouldBe(1L)
        false.coerceToNumber().shouldBe(0L)
        listOf<Instant>().coerceToNumber().shouldBe(0L)
    }

    @Test
    fun testCoerceToInstant() {
        null.coerceToInstant().shouldBe(Instant.ofEpochMilli(0L))
        "Hallo".coerceToInstant().shouldBe(Instant.ofEpochMilli(0L))
        "2022-02-27T00:00:00Z".coerceToInstant().shouldBe(Instant.ofEpochSecond(1645920000))
        42.coerceToInstant().shouldBe(Instant.ofEpochSecond(42L))
        1645920000L.coerceToInstant().shouldBe(Instant.ofEpochSecond(1645920000L))
        1645920000000L.coerceToInstant().shouldBe(Instant.ofEpochMilli(1645920000000L))
        3.1415.coerceToInstant().shouldBe(Instant.ofEpochSecond(3L))
        Instant.ofEpochMilli(123456789012L).coerceToInstant().shouldBe(Instant.ofEpochMilli(123456789012L))
        "Hallo".toByteArray(StandardCharsets.UTF_8).coerceToInstant().shouldBe(Instant.ofEpochMilli(1819042120000L))
        true.coerceToInstant().shouldBe(Instant.ofEpochSecond(1L))
        false.coerceToInstant().shouldBe(Instant.ofEpochSecond(0L))
        listOf<Instant>().coerceToInstant().shouldBe(Instant.ofEpochMilli(0L))
    }

    @Test
    fun testCoerceToBoolean() {
        null.coerceToBoolean().shouldBe(false)
        "Hallo".coerceToBoolean().shouldBe(true)
        "true".coerceToBoolean().shouldBe(true)
        "1".coerceToBoolean().shouldBe(true)
        "false".coerceToBoolean().shouldBe(false)
        "0".coerceToBoolean().shouldBe(false)
        "".coerceToBoolean().shouldBe(false)
        42.coerceToBoolean().shouldBe(true)
        3.1415.coerceToBoolean().shouldBe(true)
        Instant.ofEpochMilli(123456789012L).coerceToBoolean().shouldBe(true)
        "Hallo".toByteArray(StandardCharsets.UTF_8).coerceToBoolean().shouldBe(true)
        true.coerceToBoolean().shouldBe(true)
        false.coerceToBoolean().shouldBe(false)
        listOf<Instant>().coerceToBoolean().shouldBe(true)
    }

    @Test
    fun testCoerceToComparable() {
        null.coerceToComparable().shouldBe("")
        "Hallo".coerceToComparable().shouldBe("Hallo")
        42.coerceToComparable().shouldBe(42)
        3.1415.coerceToComparable().shouldBe(3.1415)
        Instant.ofEpochMilli(123456789012L).coerceToComparable().shouldBe(Instant.ofEpochMilli(123456789012L))
        "Hallo".toByteArray(StandardCharsets.UTF_8).coerceToComparable().shouldBe("Hallo")
        true.coerceToComparable().shouldBe(true)
        false.coerceToComparable().shouldBe(false)
        listOf<Instant>().coerceToComparable().shouldBe("[]")
    }

    @Test
    fun testCoerceToJsonNode() {
        null.coerceToJsonNode().toString().shouldBe("null")
        "Hallo".coerceToJsonNode().toString().shouldBe("\"Hallo\"")
        42L.coerceToJsonNode().toString().shouldBe("42")
        3.1415.coerceToJsonNode().toString().shouldBe("3.1415")
        Instant.ofEpochMilli(123456789012L).coerceToJsonNode().toString().shouldBe("123456789012")
        "Hallo".toByteArray(StandardCharsets.UTF_8).coerceToJsonNode().toString().shouldBe("\"SGFsbG8=\"")
        true.coerceToJsonNode().toString().shouldBe("true")
        false.coerceToJsonNode().toString().shouldBe("false")
        listOf(42L).coerceToJsonNode().toString().shouldBe("[42]")
        mapOf(42L to 43L).coerceToJsonNode().toString().shouldBe("{\"42\":43}")
    }

    @Test
    fun testCoerceToByteArray() {
        null.coerceToByteArray().shouldBe(byteArrayOf())
        "Hallo".coerceToByteArray().shouldBe(byteArrayOf(72, 97, 108, 108, 111))
        42.coerceToByteArray().shouldBe(byteArrayOf(42, 0, 0, 0, 0, 0, 0, 0))
        3.1415.coerceToByteArray().shouldBe(byteArrayOf(3, 0, 0, 0, 0, 0, 0, 0))
        Instant.ofEpochMilli(123456789012L).coerceToByteArray().shouldBe(byteArrayOf(20, 26, -103, -66, 28, 0, 0, 0))
        "Hallo".toByteArray(StandardCharsets.UTF_8).coerceToByteArray().shouldBe(byteArrayOf(72, 97, 108, 108, 111))
        true.coerceToByteArray().shouldBe(byteArrayOf(1))
        false.coerceToByteArray().shouldBe(byteArrayOf(0))
        listOf<Instant>().coerceToByteArray().shouldBe(byteArrayOf(91, 93))
    }

    @Test
    fun testCoerceToString() {
        null.coerceToString().shouldBe("")
        "Hallo".coerceToString().shouldBe("Hallo")
        42.coerceToString().shouldBe("42")
        3.1415.coerceToString().shouldBe("3.1415")
        Instant.ofEpochMilli(123456789012L).coerceToString().shouldBe("1973-11-29T21:33:09.012Z")
        "Hallo".toByteArray(StandardCharsets.UTF_8).coerceToString().shouldBe("Hallo")
        true.coerceToString().shouldBe("true")
        false.coerceToString().shouldBe("false")
        listOf<Instant>().coerceToString().shouldBe("[]")
    }
}
