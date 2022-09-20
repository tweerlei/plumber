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

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.*

class TypeConversionsTest {

    @Test
    fun testToValue() {
        null.toValue().shouldBe(NullValue.INSTANCE)
        "Hallo".toValue().shouldBe(StringValue("Hallo"))
        42.toByte().toValue().shouldBe(LongValue(42L))
        42.toChar().toValue().shouldBe(StringValue("*"))
        42.toShort().toValue().shouldBe(LongValue(42L))
        42.toValue().shouldBe(LongValue(42L))
        42L.toValue().shouldBe(LongValue(42L))
        3.1415f.toValue().shouldBe(DoubleValue(3.1415f.toDouble()))
        3.1415.toValue().shouldBe(DoubleValue(3.1415))
        Instant.ofEpochMilli(123456789012L).toValue().shouldBe(InstantValue(Instant.ofEpochMilli(123456789012L)))
        Date(123456789012L).toValue().shouldBe(InstantValue(Instant.ofEpochMilli(123456789012L)))
        Duration.ofMillis(123456789012L).toValue().shouldBe(DurationValue(Duration.ofMillis(123456789012L)))
        "Hallo".toByteArray(StandardCharsets.UTF_8).toValue().shouldBe(ByteArrayValue("Hallo".toByteArray(StandardCharsets.UTF_8)))
        true.toValue().shouldBe(BooleanValue.TRUE)
        false.toValue().shouldBe(BooleanValue.FALSE)
        with(Instant.now()) {
            listOf(this).toValue().shouldBe(Record.of("0" to InstantValue(this)))
        }
        mapOf("key1" to 42).toValue().shouldBe(Record.of("key1" to LongValue(42L)))
    }

    @Test
    fun testToComparableValue() {
        null.toComparableValue().shouldBe(NullValue.INSTANCE)
        "null".toComparableValue().shouldBe(NullValue.INSTANCE)
        "true".toComparableValue().shouldBe(BooleanValue.TRUE)
        "false".toComparableValue().shouldBe(BooleanValue.FALSE)
        "0".toComparableValue().shouldBe(LongValue(0L))
        "1".toComparableValue().shouldBe(LongValue(1L))
        "-1".toComparableValue().shouldBe(LongValue(-1L))
        "0.0".toComparableValue().shouldBe(DoubleValue(0.0))
        "3.1415".toComparableValue().shouldBe(DoubleValue(3.1415))
        "-3.1415".toComparableValue().shouldBe(DoubleValue(-3.1415))
        "2e".toComparableValue().shouldBe(StringValue("2e"))
        "2e7".toComparableValue().shouldBe(DoubleValue(20000000.0))
        "2022-02-27T00:00:00Z".toComparableValue().shouldBe(InstantValue(Instant.ofEpochSecond(1645920000)))
        "P5DT10H23M46S".toComparableValue().shouldBe(DurationValue(Duration.ofSeconds(469426)))
    }

    @Test
    fun testIfEmptyGetFrom() {
        val item = WorkItem.from(42, "foo" to "bar")

        "Test".ifEmptyGetFrom(item).shouldBe("Test")
        "".ifEmptyGetFrom(item).shouldBe("42")
        "".ifEmptyGetFrom(item, "foo").shouldBe("bar")
    }
}
