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

class DoubleValueTest {

    @Test
    fun testNonzero() {
        with (DoubleValue(42.1)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(42.1)
            toBoolean().shouldBeTrue()
            toLong().shouldBe(42L)
            toDouble().shouldBe(42.1)
            toBigInteger().shouldBe(BigInteger.valueOf(42L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(42.1))
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(42, 0, 0, 0, 0, 0, 0, 0)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isDouble.shouldBeTrue()
                doubleValue().shouldBe(42.1)
            }
            toString().shouldBe("42.1")
            size().shouldBe(4L)
            hashCode().shouldBe(42.1.hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(DoubleValue(42.1)).shouldBeTrue()
            equals(DoubleValue(0.0)).shouldBeFalse()
            equals(42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue("0")).shouldBeFalse()
            equals(StringValue("42.1")).shouldBeTrue()
            equals(StringValue("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(1)
            compareTo(DoubleValue(42.1)).shouldBe(0)
            compareTo(DoubleValue(0.0)).shouldBe(1)
            compareTo(DoubleValue(100.0)).shouldBe(-1)
            compareTo(StringValue("")).shouldBe(1)
            compareTo(StringValue("42.1")).shouldBe(0)
            compareTo(StringValue("0")).shouldBe(1)
        }
    }

    @Test
    fun testZero() {
        with (DoubleValue(0.0)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(0.0)
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
                isDouble.shouldBeTrue()
                doubleValue().shouldBe(0.0)
            }
            toString().shouldBe("0.0")
            size().shouldBe(3L)
            hashCode().shouldBe(0.0.hashCode())

            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(DoubleValue(42.0)).shouldBeFalse()
            equals(DoubleValue(0.0)).shouldBeTrue()
            equals(42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue("42.0")).shouldBeFalse()
            equals(StringValue("0.0")).shouldBeTrue()
            equals(StringValue("")).shouldBeTrue()

            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(DoubleValue(42.0)).shouldBe(-1)
            compareTo(DoubleValue(0.0)).shouldBe(0)
            compareTo(DoubleValue(100.0)).shouldBe(-1)
            compareTo(StringValue("")).shouldBe(0)
            compareTo(StringValue("42.0")).shouldBe(-1)
            compareTo(StringValue("0.0")).shouldBe(0)
        }
    }

    @Test
    fun testNegative() {
        with (DoubleValue(-42.1)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(-42.1)
            toBoolean().shouldBeTrue()
            toLong().shouldBe(-42L)
            toDouble().shouldBe(-42.1)
            toBigInteger().shouldBe(BigInteger.valueOf(-42L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(-42.1))
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(-42, -1, -1, -1, -1, -1, -1, -1)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isDouble.shouldBeTrue()
                doubleValue().shouldBe(-42.1)
            }
            toString().shouldBe("-42.1")
            size().shouldBe(5L)
            hashCode().shouldBe((-42.1).hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(DoubleValue(-42.1)).shouldBeTrue()
            equals(DoubleValue(0.0)).shouldBeFalse()
            equals(-42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue("-42.1")).shouldBeTrue()
            equals(StringValue("0")).shouldBeFalse()
            equals(StringValue("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(-1)
            compareTo(DoubleValue(-42.1)).shouldBe(0)
            compareTo(DoubleValue(0.0)).shouldBe(-1)
            compareTo(DoubleValue(-100.0)).shouldBe(1)
            compareTo(StringValue("")).shouldBe(-1)
            compareTo(StringValue("-42.1")).shouldBe(0)
            compareTo(StringValue("0")).shouldBe(-1)
        }
    }
}
