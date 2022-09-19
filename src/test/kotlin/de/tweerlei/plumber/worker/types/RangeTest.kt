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

class RangeTest {

    @Test
    fun testEquals() {

        Range.from(null, null).shouldBe(Range(NullValue.INSTANCE, NullValue.INSTANCE))
        Range.from(1, null).shouldBe(Range(LongValue(1L), NullValue.INSTANCE))
        Range.from(null, 2).shouldBe(Range(NullValue.INSTANCE, LongValue(2L)))
        Range.from(1, 2).shouldBe(Range(LongValue(1L), LongValue(2L)))
        Range.from(1L, 2L).shouldBe(Range(LongValue(1L), LongValue(2L)))
        Range.from("1", "2").shouldBe(Range(StringValue("1"), StringValue("2")))
    }

    @Test
    fun testContains() {

        Range.from(null, null).contains(NullValue.INSTANCE).shouldBeFalse()
        Range.from(null, null).contains(LongValue(42L)).shouldBeTrue()
        Range.from(null, null).contains(StringValue("42")).shouldBeTrue()

        Range.from(-10, 10).contains(NullValue.INSTANCE).shouldBeFalse()
        Range.from(-10, 10).contains(LongValue(-10L)).shouldBeFalse()
        Range.from(-10, 10).contains(LongValue(-9L)).shouldBeTrue()
        Range.from(-10, 10).contains(LongValue(10L)).shouldBeTrue()
        Range.from(-10, 10).contains(LongValue(11L)).shouldBeFalse()
        Range.from(-10, 10).contains(StringValue("05")).shouldBeTrue()
        Range.from(-10, 10).contains(StringValue("xyz")).shouldBeFalse()

        Range.from(10, -10).contains(NullValue.INSTANCE).shouldBeFalse()
        Range.from(10, -10).contains(LongValue(10L)).shouldBeFalse()
        Range.from(10, -10).contains(LongValue(9L)).shouldBeTrue()
        Range.from(10, -10).contains(LongValue(-10L)).shouldBeTrue()
        Range.from(10, -10).contains(LongValue(-11L)).shouldBeFalse()
        Range.from(10, -10).contains(StringValue("05")).shouldBeTrue()
        Range.from(10, -10).contains(StringValue("xyz")).shouldBeFalse()

        Range.from("a", "z").contains(NullValue.INSTANCE).shouldBeFalse()
        Range.from("a", "z").contains(StringValue("a")).shouldBeFalse()
        Range.from("a", "z").contains(StringValue("aa")).shouldBeTrue()
        Range.from("a", "z").contains(StringValue("b")).shouldBeTrue()
        Range.from("a", "z").contains(StringValue("bb")).shouldBeTrue()
        Range.from("a", "z").contains(StringValue("z")).shouldBeTrue()
        Range.from("a", "z").contains(StringValue("zz")).shouldBeFalse()
    }
}
