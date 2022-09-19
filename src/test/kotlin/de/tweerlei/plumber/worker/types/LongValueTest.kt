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
import org.junit.jupiter.api.Test

class LongValueTest {

    @Test
    fun testNonzero() {
        with (LongValue(42L)) {
            toAny().shouldBe(42L)
            toBoolean().shouldBeTrue()
            toNumber().shouldBe(42L)
            toNumberOrNull().shouldBe(42L)
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(42, 0, 0, 0, 0, 0, 0, 0)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isLong.shouldBeTrue()
                longValue().shouldBe(42L)
            }
            toString().shouldBe("42")
            toStringOrNull().shouldBe("42")
            size().shouldBe(2L)
            hashCode().shouldBe(42L.hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(LongValue(42L)).shouldBeTrue()
            equals(LongValue(0L)).shouldBeFalse()
            equals(42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue("0")).shouldBeFalse()
            equals(StringValue("42")).shouldBeTrue()
            equals(StringValue("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(1)
            compareTo(LongValue(42L)).shouldBe(0)
            compareTo(LongValue(0L)).shouldBe(1)
            compareTo(LongValue(100L)).shouldBe(-1)
            compareTo(StringValue("")).shouldBe(1)
            compareTo(StringValue("42")).shouldBe(0)
            compareTo(StringValue("0")).shouldBe(1)
        }
    }

    @Test
    fun testZero() {
        with (LongValue(0L)) {
            toAny().shouldBe(0L)
            toBoolean().shouldBeFalse()
            toNumber().shouldBe(0L)
            toNumberOrNull().shouldBe(0L)
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isLong.shouldBeTrue()
                longValue().shouldBe(0L)
            }
            toString().shouldBe("0")
            toStringOrNull().shouldBe("0")
            size().shouldBe(1L)
            hashCode().shouldBe(0L.hashCode())

            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(LongValue(42L)).shouldBeFalse()
            equals(LongValue(0L)).shouldBeTrue()
            equals(42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue("42")).shouldBeFalse()
            equals(StringValue("0")).shouldBeTrue()
            equals(StringValue("")).shouldBeTrue()

            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(LongValue(42L)).shouldBe(-1)
            compareTo(LongValue(0L)).shouldBe(0)
            compareTo(LongValue(100L)).shouldBe(-1)
            compareTo(StringValue("")).shouldBe(0)
            compareTo(StringValue("42")).shouldBe(-1)
            compareTo(StringValue("0")).shouldBe(0)
        }
    }

    @Test
    fun testNegative() {
        with (LongValue(-42L)) {
            toAny().shouldBe(-42L)
            toBoolean().shouldBeTrue()
            toNumber().shouldBe(-42L)
            toNumberOrNull().shouldBe(-42L)
            with(toByteArray()) {
                size.shouldBe(8)
                contentEquals(byteArrayOf(-42, -1, -1, -1, -1, -1, -1, -1)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isLong.shouldBeTrue()
                longValue().shouldBe(-42L)
            }
            toString().shouldBe("-42")
            toStringOrNull().shouldBe("-42")
            size().shouldBe(3L)
            hashCode().shouldBe((-42L).hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(LongValue(-42L)).shouldBeTrue()
            equals(LongValue(0L)).shouldBeFalse()
            equals(-42L).shouldBeFalse()
            equals(0L).shouldBeFalse()
            equals(StringValue("-42")).shouldBeTrue()
            equals(StringValue("0")).shouldBeFalse()
            equals(StringValue("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(-1)
            compareTo(LongValue(-42L)).shouldBe(0)
            compareTo(LongValue(0L)).shouldBe(-1)
            compareTo(LongValue(-100L)).shouldBe(1)
            compareTo(StringValue("")).shouldBe(-1)
            compareTo(StringValue("-42")).shouldBe(0)
            compareTo(StringValue("0")).shouldBe(-1)
        }
    }
}
