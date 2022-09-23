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

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant

class InstantValueTest {

    @Test
    fun testNonzero() {
        with (InstantValue(Instant.ofEpochMilli(42L))) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(Instant.ofEpochMilli(42L))
            toBoolean().shouldBeTrue()
            toLong().shouldBe(42L)
            toDouble().shouldBe(42.0)
            toBigInteger().shouldBe(BigInteger.valueOf(42L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(42.0))
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(42, 0, 0, 0, 0, 0, 0, 0)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isLong.shouldBeTrue()
                longValue().shouldBe(42L)
            }
            toString().shouldBe("1970-01-01T00:00:00.042Z")
            size().shouldBe(24L)
            hashCode().shouldBe(Instant.ofEpochMilli(42L).hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(InstantValue(Instant.ofEpochMilli(42L))).shouldBeTrue()
            equals(InstantValue(Instant.ofEpochMilli(0L))).shouldBeFalse()
            equals(42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue("0")).shouldBeFalse()
            equals(StringValue("42")).shouldBeTrue()
            equals(StringValue("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(1)
            compareTo(InstantValue(Instant.ofEpochMilli(42L))).shouldBe(0)
            compareTo(InstantValue(Instant.ofEpochMilli(0L))).shouldBe(1)
            compareTo(InstantValue(Instant.ofEpochMilli(100L))).shouldBe(-1)
            compareTo(StringValue("")).shouldBe(1)
            compareTo(StringValue("42")).shouldBe(0)
            compareTo(StringValue("0")).shouldBe(1)
        }
    }

    @Test
    fun testZero() {
        with (InstantValue(Instant.ofEpochMilli(0L))) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(Instant.ofEpochMilli(0L))
            toBoolean().shouldBeFalse()
            toLong().shouldBe(0L)
            toDouble().shouldBe(0.0)
            toBigInteger().shouldBe(BigInteger.valueOf(0L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(0.0))
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isLong.shouldBeTrue()
                longValue().shouldBe(0L)
            }
            toString().shouldBe("1970-01-01T00:00:00Z")
            size().shouldBe(20L)
            hashCode().shouldBe(Instant.ofEpochMilli(0L).hashCode())

            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(InstantValue(Instant.ofEpochMilli(42L))).shouldBeFalse()
            equals(InstantValue(Instant.ofEpochMilli(0L))).shouldBeTrue()
            equals(42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue("42")).shouldBeFalse()
            equals(StringValue("0")).shouldBeTrue()
            equals(StringValue("")).shouldBeTrue()

            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(InstantValue(Instant.ofEpochMilli(42L))).shouldBe(-1)
            compareTo(InstantValue(Instant.ofEpochMilli(0L))).shouldBe(0)
            compareTo(InstantValue(Instant.ofEpochMilli(100L))).shouldBe(-1)
            compareTo(StringValue("")).shouldBe(0)
            compareTo(StringValue("42")).shouldBe(-1)
            compareTo(StringValue("0")).shouldBe(0)
        }
    }
}
