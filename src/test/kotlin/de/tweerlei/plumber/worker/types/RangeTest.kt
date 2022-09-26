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

class RangeTest {

    @Test
    fun testNonEmpty() {
        with (Range.of(0L, 10L)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBeSameInstanceAs(this)
            toBoolean().shouldBeTrue()
            toLong().shouldBe(10L)
            toDouble().shouldBe(10.0)
            toBigInteger().shouldBe(BigInteger.valueOf(10L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(10.0))
            with(toByteArray()) {
                size.shouldBe(9)
//                contentEquals(byteArrayOf(10, 0, 0, 0)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isArray.shouldBeTrue()
            }
            toString().shouldBe("[0 .. 10]")
            size().shouldBe(10L)
//            hashCode().shouldBe("hello".hashCode())
        }
    }

    @Test
    fun testEquals() {

        Range().shouldBe(Range(NullValue.INSTANCE, NullValue.INSTANCE))

        val noLong: Long? = null
        Range.of(noLong, noLong).shouldBe(Range(NullValue.INSTANCE, NullValue.INSTANCE))
        Range.of(1, noLong).shouldBe(Range(LongValue.of(1L), NullValue.INSTANCE))
        Range.of(noLong, 2).shouldBe(Range(NullValue.INSTANCE, LongValue.of(2L)))
        Range.of(1, 2).shouldBe(Range(LongValue.of(1L), LongValue.of(2L)))
        Range.of(1L, 2L).shouldBe(Range(LongValue.of(1L), LongValue.of(2L)))

        val noString: String? = null
        Range.of(noString, noString).shouldBe(Range(NullValue.INSTANCE, NullValue.INSTANCE))
        Range.of("1", "2").shouldBe(Range(StringValue.of("1"), StringValue.of("2")))
    }

    @Test
    fun testContains() {

        Range().contains(NullValue.INSTANCE).shouldBeFalse()
        Range().contains(LongValue.of(42L)).shouldBeTrue()
        Range().contains(StringValue.of("42")).shouldBeTrue()

        Range.of(-10, 10).contains(NullValue.INSTANCE).shouldBeFalse()
        Range.of(-10, 10).contains(LongValue.of(-10L)).shouldBeFalse()
        Range.of(-10, 10).contains(LongValue.of(-9L)).shouldBeTrue()
        Range.of(-10, 10).contains(LongValue.of(10L)).shouldBeTrue()
        Range.of(-10, 10).contains(LongValue.of(11L)).shouldBeFalse()
        Range.of(-10, 10).contains(StringValue.of("05")).shouldBeTrue()
        Range.of(-10, 10).contains(StringValue.of("xyz")).shouldBeFalse()

        Range.of(10, -10).contains(NullValue.INSTANCE).shouldBeFalse()
        Range.of(10, -10).contains(LongValue.of(10L)).shouldBeFalse()
        Range.of(10, -10).contains(LongValue.of(9L)).shouldBeTrue()
        Range.of(10, -10).contains(LongValue.of(-10L)).shouldBeTrue()
        Range.of(10, -10).contains(LongValue.of(-11L)).shouldBeFalse()
        Range.of(10, -10).contains(StringValue.of("05")).shouldBeTrue()
        Range.of(10, -10).contains(StringValue.of("xyz")).shouldBeFalse()

        Range.of("a", "z").contains(NullValue.INSTANCE).shouldBeFalse()
        Range.of("a", "z").contains(StringValue.of("a")).shouldBeFalse()
        Range.of("a", "z").contains(StringValue.of("aa")).shouldBeTrue()
        Range.of("a", "z").contains(StringValue.of("b")).shouldBeTrue()
        Range.of("a", "z").contains(StringValue.of("bb")).shouldBeTrue()
        Range.of("a", "z").contains(StringValue.of("z")).shouldBeTrue()
        Range.of("a", "z").contains(StringValue.of("zz")).shouldBeFalse()
    }
}
