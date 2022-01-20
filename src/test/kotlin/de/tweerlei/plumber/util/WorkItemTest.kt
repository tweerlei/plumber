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
package de.tweerlei.plumber.util

import de.tweerlei.plumber.worker.WorkItem
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.time.Instant

class WorkItemTest {

    @Test
    fun testGetSetMain() {
        val item = WorkItem.of("Test")

        item.getAs<Any>().shouldBe("Test")
        item.getAs<String>().shouldBe("Test")
        item.set(42)
        item.getAs<Int>().shouldBe(42)

        shouldThrow<ClassCastException> { item.getAs<Long>() }
    }

    @Test
    fun testGetSetAlternate() {
        val item = WorkItem.of("")
        item.set("Test 1", "value1")
        item.set("Test 2", "value2")

        item.getAs<String>().shouldBe("")
        item.getAs<String>("value1").shouldBe("Test 1")
        item.getAs<String>("value2").shouldBe("Test 2")

        shouldThrow<NoSuchElementException> { item.getAs<String>("value3") }
    }

    @Test
    fun testGetString() {
        WorkItem.of(null).getString().shouldBe("")
        WorkItem.of("Hallo").getString().shouldBe("Hallo")
        WorkItem.of(42).getString().shouldBe("42")
        WorkItem.of(3.1415).getString().shouldBe("3.1415")
        WorkItem.of(Instant.ofEpochMilli(123456789012L)).getString().shouldBe("1973-11-29T21:33:09.012Z")
        WorkItem.of("Hallo".toByteArray(StandardCharsets.UTF_8)).getString().shouldBe("Hallo")
        WorkItem.of(true).getString().shouldBe("true")
        WorkItem.of(false).getString().shouldBe("false")
        WorkItem.of(listOf<Instant>()).getString().shouldBe("[]")
    }

    @Test
    fun testGetInt() {
        WorkItem.of(null).getInt().shouldBe(0)
        WorkItem.of("Hallo").getInt().shouldBe(0)
        WorkItem.of(42).getInt().shouldBe(42)
        WorkItem.of(3.1415).getInt().shouldBe(3)
        WorkItem.of(Instant.ofEpochMilli(123456789012L)).getInt().shouldBe(123456789)
        WorkItem.of("Hallo".toByteArray(StandardCharsets.UTF_8)).getInt().shouldBe(1819042120)
        WorkItem.of(true).getInt().shouldBe(1)
        WorkItem.of(false).getInt().shouldBe(0)
        WorkItem.of(listOf<Instant>()).getInt().shouldBe(0)
    }

    @Test
    fun testGetLong() {
        WorkItem.of(null).getLong().shouldBe(0L)
        WorkItem.of("Hallo").getLong().shouldBe(0L)
        WorkItem.of(42).getLong().shouldBe(42L)
        WorkItem.of(3.1415).getLong().shouldBe(3L)
        WorkItem.of(Instant.ofEpochMilli(123456789012L)).getLong().shouldBe(123456789012L)
        WorkItem.of("Hallo".toByteArray(StandardCharsets.UTF_8)).getLong().shouldBe(1819042120L)
        WorkItem.of(true).getLong().shouldBe(1L)
        WorkItem.of(false).getLong().shouldBe(0L)
        WorkItem.of(listOf<Instant>()).getLong().shouldBe(0L)
    }

    @Test
    fun testGetNumber() {
        WorkItem.of(null).getNumber().shouldBe(0L)
        WorkItem.of("Hallo").getNumber().shouldBe(0L)
        WorkItem.of(42).getNumber().shouldBe(42L)
        WorkItem.of(3.1415).getNumber().shouldBe(3.1415)
        WorkItem.of(Instant.ofEpochMilli(123456789012L)).getNumber().shouldBe(123456789012L)
        WorkItem.of("Hallo".toByteArray(StandardCharsets.UTF_8)).getNumber().shouldBe(1819042120L)
        WorkItem.of(true).getNumber().shouldBe(1L)
        WorkItem.of(false).getNumber().shouldBe(0L)
        WorkItem.of(listOf<Instant>()).getNumber().shouldBe(0L)
    }

    @Test
    fun testGetBoolean() {
        WorkItem.of(null).getBoolean().shouldBe(false)
        WorkItem.of("Hallo").getBoolean().shouldBe(true)
        WorkItem.of("true").getBoolean().shouldBe(true)
        WorkItem.of("1").getBoolean().shouldBe(true)
        WorkItem.of("false").getBoolean().shouldBe(false)
        WorkItem.of("0").getBoolean().shouldBe(false)
        WorkItem.of("").getBoolean().shouldBe(false)
        WorkItem.of(42).getBoolean().shouldBe(true)
        WorkItem.of(3.1415).getBoolean().shouldBe(true)
        WorkItem.of(Instant.ofEpochMilli(123456789012L)).getBoolean().shouldBe(true)
        WorkItem.of("Hallo".toByteArray(StandardCharsets.UTF_8)).getBoolean().shouldBe(true)
        WorkItem.of(true).getBoolean().shouldBe(true)
        WorkItem.of(false).getBoolean().shouldBe(false)
        WorkItem.of(listOf<Instant>()).getBoolean().shouldBe(true)
    }

    @Test
    fun testGetInstant() {
        WorkItem.of(null).getInstant().shouldBe(Instant.ofEpochMilli(0L))
        WorkItem.of("Hallo").getInstant().shouldBe(Instant.ofEpochMilli(0L))
        WorkItem.of(42).getInstant().shouldBe(Instant.ofEpochSecond(42L))
        WorkItem.of(3.1415).getInstant().shouldBe(Instant.ofEpochMilli(3L))
        WorkItem.of(Instant.ofEpochMilli(123456789012L)).getInstant().shouldBe(Instant.ofEpochMilli(123456789012L))
        WorkItem.of("Hallo".toByteArray(StandardCharsets.UTF_8)).getInstant().shouldBe(Instant.ofEpochMilli(1819042120L))
        WorkItem.of(true).getInstant().shouldBe(Instant.ofEpochSecond(1L))
        WorkItem.of(false).getInstant().shouldBe(Instant.ofEpochSecond(0L))
        WorkItem.of(listOf<Instant>()).getInstant().shouldBe(Instant.ofEpochMilli(0L))
    }

    @Test
    fun testGetByteArray() {
        WorkItem.of(null).getByteArray().shouldBe(byteArrayOf())
        WorkItem.of("Hallo").getByteArray().shouldBe(byteArrayOf(72, 97, 108, 108, 111))
        WorkItem.of(42).getByteArray().shouldBe(byteArrayOf(42, 0, 0, 0))
        WorkItem.of(3.1415).getByteArray().shouldBe(byteArrayOf(3, 0, 0, 0, 0, 0, 0, 0))
        WorkItem.of(Instant.ofEpochMilli(123456789012L)).getByteArray().shouldBe(byteArrayOf(20, 26, -103, -66, 28, 0, 0, 0))
        WorkItem.of("Hallo".toByteArray(StandardCharsets.UTF_8)).getByteArray().shouldBe(byteArrayOf(72, 97, 108, 108, 111))
        WorkItem.of(true).getByteArray().shouldBe(byteArrayOf(1))
        WorkItem.of(false).getByteArray().shouldBe(byteArrayOf(0))
        WorkItem.of(listOf<Instant>()).getByteArray().shouldBe(byteArrayOf(91, 93))
    }
}
