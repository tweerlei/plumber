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
import org.junit.jupiter.api.Test

class ByteArrayValueTest {

    @Test
    fun testNonEmpty() {
        val value = byteArrayOf(104, 101, 108, 108, 111)
        with (ByteArrayValue(value)) {
            toAny().shouldBe(value)
            toBoolean().shouldBeTrue()
            toNumber().shouldBe(1819043176L)
            toNumberOrNull().shouldBe(1819043176L)
            with(toByteArray()) {
                size.shouldBe(5)
                contentEquals(value).shouldBeTrue()
            }
            with (toJsonNode()) {
                isBinary.shouldBeTrue()
                binaryValue().contentEquals(value).shouldBeTrue()
            }
            toString().shouldBe("hello")
            toStringOrNull().shouldBe("hello")
            size().shouldBe(5L)
            hashCode().shouldBe(value.contentHashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(ByteArrayValue(value)).shouldBeTrue()
            equals(ByteArrayValue(byteArrayOf(23))).shouldBeFalse()
            equals("hello").shouldBeFalse()
            equals("HELLO").shouldBeFalse()
            equals(LongValue(1L)).shouldBeFalse()
            equals(StringValue("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(5)
            compareTo(ByteArrayValue(value)).shouldBe(0)
            compareTo(ByteArrayValue(byteArrayOf(23))).shouldBe(81)
            compareTo(LongValue(0L)).shouldBe(104)
        }
    }

    @Test
    fun testEmpty() {
        val value = byteArrayOf()
        with (ByteArrayValue(value)) {
            toAny().shouldBe(value)
            toBoolean().shouldBeFalse()
            toNumber().shouldBe(0L)
            toNumberOrNull().shouldBe(0L)
            with(toByteArray()) {
                size.shouldBe(0)
            }
            with (toJsonNode()) {
                isBinary.shouldBeTrue()
                binaryValue().size.shouldBe(0)
            }
            toString().shouldBe("")
            toStringOrNull().shouldBe("")
            size().shouldBe(0L)
            hashCode().shouldBe(value.contentHashCode())

            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(ByteArrayValue(value)).shouldBeTrue()
            equals(ByteArrayValue(byteArrayOf(0))).shouldBeFalse()
            equals("").shouldBeFalse()
            equals("HELLO").shouldBeFalse()
            equals(LongValue(0L)).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(ByteArrayValue(value)).shouldBe(0)
            compareTo(ByteArrayValue(byteArrayOf(0))).shouldBe(-1)
            compareTo(LongValue(0L)).shouldBe(-8)
        }
    }

    @Test
    fun testToBoolean() {
        ByteArrayValue(byteArrayOf()).toBoolean().shouldBeFalse()
        ByteArrayValue(byteArrayOf(0, 0, 0, 0)).toBoolean().shouldBeFalse()
        ByteArrayValue(byteArrayOf(1)).toBoolean().shouldBeTrue()
        ByteArrayValue(byteArrayOf(0, 0, 2, 0)).toBoolean().shouldBeTrue()
    }

    @Test
    fun testToNumber() {
        ByteArrayValue(byteArrayOf()).toNumber().shouldBe(0L)
        ByteArrayValue(byteArrayOf(0)).toNumber().shouldBe(0L)
        ByteArrayValue(byteArrayOf(5)).toNumber().shouldBe(5L)
        ByteArrayValue(byteArrayOf(0, 0)).toNumber().shouldBe(0L)
        ByteArrayValue(byteArrayOf(1, 2)).toNumber().shouldBe(513L)
        ByteArrayValue(byteArrayOf(1, 2, 3)).toNumber().shouldBe(513L)
        ByteArrayValue(byteArrayOf(0, 0, 0, 0)).toNumber().shouldBe(0L)
        ByteArrayValue(byteArrayOf(1, 2, 3, 4)).toNumber().shouldBe(67305985L)
        ByteArrayValue(byteArrayOf(1, 2, 3, 4, 5)).toNumber().shouldBe(67305985L)
        ByteArrayValue(byteArrayOf(1, 2, 3, 4, 5, 6)).toNumber().shouldBe(67305985L)
        ByteArrayValue(byteArrayOf(1, 2, 3, 4, 5, 6, 7)).toNumber().shouldBe(67305985L)
        ByteArrayValue(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)).toNumber().shouldBe(0L)
        ByteArrayValue(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)).toNumber().shouldBe(578437695752307201L)
        ByteArrayValue(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).toNumber().shouldBe(578437695752307201L)
    }
}
