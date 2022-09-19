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

class OtherValueTest {

    @Test
    fun testIt() {
        val value = listOf(1, 2, 3)
        with (OtherValue(value)) {
            toAny().shouldBe(value)
            toBoolean().shouldBeTrue()
            toNumber().shouldBe(0L)
            toNumberOrNull().shouldBe(0L)
            with (toByteArray()) {
                size.shouldBe(9)
                contentEquals(byteArrayOf(91, 49, 44, 32, 50, 44, 32, 51, 93)).shouldBeTrue()
            }
            with (toJsonNode()) {
                isTextual.shouldBeTrue()
                textValue().shouldBe("[1, 2, 3]")
            }
            toString().shouldBe("[1, 2, 3]")
            toStringOrNull().shouldBe("[1, 2, 3]")
            size().shouldBe(9L)
            hashCode().shouldBe(value.hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(value).shouldBeFalse()
            equals(null).shouldBeFalse()
            equals("").shouldBeFalse()
            equals(StringValue("[1, 2, 3]")).shouldBeFalse()
        }
    }
}
