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
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant

class LongValueTest {

    @Test
    fun testNonzero() {
        with (LongValue.of(42L)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(42L)
            toBoolean().shouldBeTrue()
            toLong().shouldBe(42L)
            toDouble().shouldBe(42.0)
            toBigInteger().shouldBe(BigInteger.valueOf(42L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(42.0))
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(42, 0, 0, 0, 0, 0, 0, 0)).shouldBeTrue()
            }
            toRange().shouldBe(Range(this, NullValue.INSTANCE))
            with (toRecord()) {
                size().shouldBe(1)
                getValue("0").toAny().shouldBe(42L)
            }
            with (toJsonNode()) {
                isLong.shouldBeTrue()
                longValue().shouldBe(42L)
            }
            toString().shouldBe("42")
            size().shouldBe(2L)
            hashCode().shouldBe(42L.hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(LongValue.of(42L)).shouldBeTrue()
            equals(LongValue.of(0L)).shouldBeFalse()
            equals(42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue.of("0")).shouldBeFalse()
            equals(StringValue.of("42")).shouldBeTrue()
            equals(StringValue.of("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(1)
            compareTo(LongValue.of(42L)).shouldBe(0)
            compareTo(LongValue.of(0L)).shouldBe(1)
            compareTo(LongValue.of(100L)).shouldBe(-1)
            compareTo(StringValue.of("")).shouldBe(1)
            compareTo(StringValue.of("42")).shouldBe(0)
            compareTo(StringValue.of("0")).shouldBe(1)
        }
    }

    @Test
    fun testZero() {
        with (LongValue.of(0L)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(0L)
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
            toString().shouldBe("0")
            size().shouldBe(1L)
            hashCode().shouldBe(0L.hashCode())

            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(LongValue.of(42L)).shouldBeFalse()
            equals(LongValue.of(0L)).shouldBeTrue()
            equals(42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue.of("42")).shouldBeFalse()
            equals(StringValue.of("0")).shouldBeTrue()
            equals(StringValue.of("")).shouldBeTrue()

            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(LongValue.of(42L)).shouldBe(-1)
            compareTo(LongValue.of(0L)).shouldBe(0)
            compareTo(LongValue.of(100L)).shouldBe(-1)
            compareTo(StringValue.of("")).shouldBe(0)
            compareTo(StringValue.of("42")).shouldBe(-1)
            compareTo(StringValue.of("0")).shouldBe(0)
        }
    }

    @Test
    fun testNegative() {
        with (LongValue.of(-42L)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(-42L)
            toBoolean().shouldBeTrue()
            toLong().shouldBe(-42L)
            toDouble().shouldBe(-42.0)
            toBigInteger().shouldBe(BigInteger.valueOf(-42L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(-42.0))
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(-42, -1, -1, -1, -1, -1, -1, -1)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isLong.shouldBeTrue()
                longValue().shouldBe(-42L)
            }
            toString().shouldBe("-42")
            size().shouldBe(3L)
            hashCode().shouldBe((-42L).hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(LongValue.of(-42L)).shouldBeTrue()
            equals(LongValue.of(0L)).shouldBeFalse()
            equals(-42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue.of("-42")).shouldBeTrue()
            equals(StringValue.of("0")).shouldBeFalse()
            equals(StringValue.of("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(-1)
            compareTo(LongValue.of(-42L)).shouldBe(0)
            compareTo(LongValue.of(0L)).shouldBe(-1)
            compareTo(LongValue.of(-100L)).shouldBe(1)
            compareTo(StringValue.of("")).shouldBe(-1)
            compareTo(StringValue.of("-42")).shouldBe(0)
            compareTo(StringValue.of("0")).shouldBe(-1)
        }
    }
}
