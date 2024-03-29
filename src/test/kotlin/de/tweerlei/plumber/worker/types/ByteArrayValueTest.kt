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

class ByteArrayValueTest {

    @Test
    fun testNonEmpty() {
        val value = byteArrayOf(104, 101, 108, 108, 111)
        with (ByteArrayValue.of(value)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(value)
            toBoolean().shouldBeTrue()
            toLong().shouldBe(1819043176L)
            toDouble().shouldBe(1819043176.0)
            toBigInteger().shouldBe(BigInteger.valueOf(1819043176L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(1819043176L))
            with(toByteArray()) {
                size.shouldBe(5)
                contentEquals(value).shouldBeTrue()
            }
            toRange().shouldBe(Range(this, NullValue.INSTANCE))
            with (toRecord()) {
                size().shouldBe(1)
                getValue("0").toAny().shouldBe(value)
            }
            with (toJsonNode()) {
                isBinary.shouldBeTrue()
                binaryValue().contentEquals(value).shouldBeTrue()
            }
            toString().shouldBe("hello")
            size().shouldBe(5L)
            hashCode().shouldBe(value.contentHashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(ByteArrayValue.of(value)).shouldBeTrue()
            equals(ByteArrayValue.of(byteArrayOf(23))).shouldBeFalse()
            equals("hello").shouldBeFalse()
            equals("HELLO").shouldBeFalse()
            equals(LongValue.of(1L)).shouldBeFalse()
            equals(StringValue.of("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(5)
            compareTo(ByteArrayValue.of(value)).shouldBe(0)
            compareTo(ByteArrayValue.of(byteArrayOf(23))).shouldBe(81)
            compareTo(LongValue.of(0L)).shouldBe(104)
        }
    }

    @Test
    fun testEmpty() {
        val value = byteArrayOf()
        with (ByteArrayValue.of(value)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(value)
            toBoolean().shouldBeFalse()
            toLong().shouldBe(0L)
            toDouble().shouldBe(0.0)
            toBigInteger().shouldBe(BigInteger.valueOf(0L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(0.0))
            with(toByteArray()) {
                size.shouldBe(0)
            }
            with (toJsonNode()) {
                isBinary.shouldBeTrue()
                binaryValue().size.shouldBe(0)
            }
            toString().shouldBe("")
            size().shouldBe(0L)
            hashCode().shouldBe(value.contentHashCode())

            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(ByteArrayValue.of(value)).shouldBeTrue()
            equals(ByteArrayValue.of(byteArrayOf(0))).shouldBeFalse()
            equals("").shouldBeFalse()
            equals("HELLO").shouldBeFalse()
            equals(LongValue.of(0L)).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(ByteArrayValue.of(value)).shouldBe(0)
            compareTo(ByteArrayValue.of(byteArrayOf(0))).shouldBe(-1)
            compareTo(LongValue.of(0L)).shouldBe(-8)
        }
    }

    @Test
    fun testToBoolean() {
        ByteArrayValue.of(byteArrayOf()).toBoolean().shouldBeFalse()
        ByteArrayValue.of(byteArrayOf(0, 0, 0, 0)).toBoolean().shouldBeFalse()
        ByteArrayValue.of(byteArrayOf(1)).toBoolean().shouldBeTrue()
        ByteArrayValue.of(byteArrayOf(0, 0, 2, 0)).toBoolean().shouldBeTrue()
    }

    @Test
    fun testToLong() {
        ByteArrayValue.of(byteArrayOf()).toLong().shouldBe(0L)
        ByteArrayValue.of(byteArrayOf(0)).toLong().shouldBe(0L)
        ByteArrayValue.of(byteArrayOf(5)).toLong().shouldBe(5L)
        ByteArrayValue.of(byteArrayOf(0, 0)).toLong().shouldBe(0L)
        ByteArrayValue.of(byteArrayOf(1, 2)).toLong().shouldBe(513L)
        ByteArrayValue.of(byteArrayOf(1, 2, 3)).toLong().shouldBe(513L)
        ByteArrayValue.of(byteArrayOf(0, 0, 0, 0)).toLong().shouldBe(0L)
        ByteArrayValue.of(byteArrayOf(1, 2, 3, 4)).toLong().shouldBe(67305985L)
        ByteArrayValue.of(byteArrayOf(1, 2, 3, 4, 5)).toLong().shouldBe(67305985L)
        ByteArrayValue.of(byteArrayOf(1, 2, 3, 4, 5, 6)).toLong().shouldBe(67305985L)
        ByteArrayValue.of(byteArrayOf(1, 2, 3, 4, 5, 6, 7)).toLong().shouldBe(67305985L)
        ByteArrayValue.of(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)).toLong().shouldBe(0L)
        ByteArrayValue.of(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)).toLong().shouldBe(578437695752307201L)
        ByteArrayValue.of(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).toLong().shouldBe(578437695752307201L)
    }
}
