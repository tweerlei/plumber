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

class StringValueTest {

    @Test
    fun testNonEmpty() {
        with (StringValue("hello")) {
            toAny().shouldBe("hello")
            toBoolean().shouldBeTrue()
            toNumber().shouldBe(0L)
            toNumberOrNull().shouldBe(0L)
            with(toByteArray()) {
                size.shouldBe(5)
                contentEquals(byteArrayOf(104, 101, 108, 108, 111)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isTextual.shouldBeTrue()
                textValue().shouldBe("hello")
            }
            toString().shouldBe("hello")
            toStringOrNull().shouldBe("hello")
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
            toAny().shouldBe("")
            toBoolean().shouldBeFalse()
            toNumber().shouldBe(0L)
            toNumberOrNull().shouldBe(0L)
            with(toByteArray()) {
                size.shouldBe(0)
            }
            with (toJsonNode()) {
                isTextual.shouldBeTrue()
                textValue().shouldBe("")
            }
            toString().shouldBe("")
            toStringOrNull().shouldBe("")
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
    fun testToNumber() {
        StringValue("").toNumber().shouldBe(0L)
        StringValue("0").toNumber().shouldBe(0L)
        StringValue("1").toNumber().shouldBe(1L)
        StringValue("-1").toNumber().shouldBe(-1L)
        StringValue("foo").toNumber().shouldBe(0L)
        StringValue("3a").toNumber().shouldBe(0L)

        StringValue("0.0").toNumber().shouldBe(0.0)
        StringValue("0.1").toNumber().shouldBe(0.1)
        StringValue("2.1").toNumber().shouldBe(2.1)
        StringValue("3e2").toNumber().shouldBe(300.0)
    }
}
