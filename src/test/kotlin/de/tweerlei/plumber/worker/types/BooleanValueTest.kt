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

class BooleanValueTest {

    @Test
    fun testCreate() {
        BooleanValue.of(true).value.shouldBeTrue()
        BooleanValue.of(false).value.shouldBeFalse()
    }

    @Test
    fun testTrue() {
        with (BooleanValue.TRUE) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBeTrue()
            toBoolean().shouldBeTrue()
            toLong().shouldBe(1L)
            toDouble().shouldBe(1.0)
            toBigInteger().shouldBe(BigInteger.valueOf(1L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(1.0))
            with(toByteArray()) {
                size.shouldBe(1)
                get(0).shouldBe(1)
            }
            with (toJsonNode()) {
                isBoolean.shouldBeTrue()
                booleanValue().shouldBeTrue()
            }
            toString().shouldBe("true")
            size().shouldBe(1L)
            hashCode().shouldBe(true.hashCode())

            equals(BooleanValue.TRUE).shouldBeTrue()
            equals(BooleanValue.FALSE).shouldBeFalse()
            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(true).shouldBeFalse()
            equals(false).shouldBeFalse()
            equals(LongValue(1L)).shouldBeTrue()
            equals(StringValue("")).shouldBeFalse()

            compareTo(BooleanValue.TRUE).shouldBe(0)
            compareTo(BooleanValue.FALSE).shouldBe(1)
            compareTo(NullValue.INSTANCE).shouldBe(1)
            compareTo(StringValue("")).shouldBe(1)
            compareTo(LongValue(0L)).shouldBe(1)
        }
    }

    @Test
    fun testFalse() {
        with (BooleanValue.FALSE) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBeFalse()
            toBoolean().shouldBeFalse()
            toLong().shouldBe(0L)
            toDouble().shouldBe(0.0)
            toBigInteger().shouldBe(BigInteger.valueOf(0L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(0.0))
            with(toByteArray()) {
                size.shouldBe(1)
                get(0).shouldBe(0)
            }
            with (toJsonNode()) {
                isBoolean.shouldBeTrue()
                booleanValue().shouldBeFalse()
            }
            toString().shouldBe("false")
            size().shouldBe(1L)
            hashCode().shouldBe(false.hashCode())

            equals(BooleanValue.TRUE).shouldBeFalse()
            equals(BooleanValue.FALSE).shouldBeTrue()
            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(true).shouldBeFalse()
            equals(false).shouldBeFalse()
            equals(LongValue(1L)).shouldBeFalse()
            equals(StringValue("")).shouldBeTrue()

            compareTo(BooleanValue.TRUE).shouldBe(-1)
            compareTo(BooleanValue.FALSE).shouldBe(0)
            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(StringValue("")).shouldBe(0)
            compareTo(LongValue(0L)).shouldBe(0)
        }
    }
}
