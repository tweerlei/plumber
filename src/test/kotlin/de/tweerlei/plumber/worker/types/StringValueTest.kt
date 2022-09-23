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

class StringValueTest {

    @Test
    fun testNonEmpty() {
        with (StringValue("hello")) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe("hello")
            toBoolean().shouldBeTrue()
            toLong().shouldBe(0L)
            toDouble().shouldBe(0.0)
            toBigInteger().shouldBe(BigInteger.valueOf(0L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(0.0))
            with(toByteArray()) {
                size.shouldBe(5)
                contentEquals(byteArrayOf(104, 101, 108, 108, 111)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isTextual.shouldBeTrue()
                textValue().shouldBe("hello")
            }
            toString().shouldBe("hello")
            size().shouldBe(5L)
            hashCode().shouldBe("hello".hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(StringValue("hello")).shouldBeTrue()
            equals(StringValue("HELLO")).shouldBeFalse()
            equals("hello").shouldBeFalse()
            equals("HELLO").shouldBeFalse()
            equals(LongValue(1L)).shouldBeFalse()
            equals(StringValue("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(5)
            compareTo(StringValue("hello")).shouldBe(0)
            compareTo(StringValue("HELLO")).shouldBe(32)
            compareTo(StringValue("iello")).shouldBe(-1)
            compareTo(LongValue(0L)).shouldBe(56)
        }
    }

    @Test
    fun testEmpty() {
        with (StringValue("")) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe("")
            toBoolean().shouldBeFalse()
            toLong().shouldBe(0L)
            toDouble().shouldBe(0.0)
            toBigInteger().shouldBe(BigInteger.valueOf(0L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(0.0))
            with(toByteArray()) {
                size.shouldBe(0)
            }
            with (toJsonNode()) {
                isTextual.shouldBeTrue()
                textValue().shouldBe("")
            }
            toString().shouldBe("")
            size().shouldBe(0L)
            hashCode().shouldBe("".hashCode())

            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(StringValue("")).shouldBeTrue()
            equals(StringValue("HELLO")).shouldBeFalse()
            equals("").shouldBeFalse()
            equals("HELLO").shouldBeFalse()
            equals(LongValue(0L)).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(StringValue("")).shouldBe(0)
            compareTo(StringValue("HELLO")).shouldBe(-5)
            compareTo(StringValue("iello")).shouldBe(-5)
            compareTo(LongValue(0L)).shouldBe(-1)
        }
    }

    @Test
    fun testToBoolean() {
        StringValue("").toBoolean().shouldBeFalse()
        StringValue("0").toBoolean().shouldBeFalse()
        StringValue("false").toBoolean().shouldBeFalse()
        StringValue("hello").toBoolean().shouldBeTrue()
        StringValue("1").toBoolean().shouldBeTrue()
        StringValue("true").toBoolean().shouldBeTrue()
    }

    @Test
    fun testToLong() {
        StringValue("").toLong().shouldBe(0L)
        StringValue("0").toLong().shouldBe(0L)
        StringValue("1").toLong().shouldBe(1L)
        StringValue("-1").toLong().shouldBe(-1L)
        StringValue("foo").toLong().shouldBe(0L)
        StringValue("3a").toLong().shouldBe(0L)

        StringValue("0.0").toLong().shouldBe(0L)
        StringValue("0.1").toLong().shouldBe(0L)
        StringValue("2.1").toLong().shouldBe(0L)
        StringValue("3e2").toLong().shouldBe(0L)
    }

    @Test
    fun testToDouble() {
        StringValue("0.0").toDouble().shouldBe(0.0)
        StringValue("0.1").toDouble().shouldBe(0.1)
        StringValue("2.1").toDouble().shouldBe(2.1)
        StringValue("3e2").toDouble().shouldBe(300.0)
    }
}
