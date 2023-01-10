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

class AnyValueTest {

    @Test
    fun testIt() {
        val value = listOf(1, 2, 3)
        with (AnyValue.of(value)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBe(value)
            toBoolean().shouldBeTrue()
            toLong().shouldBe(0L)
            toDouble().shouldBe(0.0)
            toBigInteger().shouldBe(BigInteger.valueOf(0L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(0.0))
            with (toByteArray()) {
                size.shouldBe(9)
                contentEquals(byteArrayOf(91, 49, 44, 32, 50, 44, 32, 51, 93)).shouldBeTrue()
            }
            with (toRecord()) {
                size.shouldBe(1)
                getValue("0").toAny().shouldBe(value)
            }
            with (toJsonNode()) {
                isTextual.shouldBeTrue()
                textValue().shouldBe("[1, 2, 3]")
            }
            toString().shouldBe("[1, 2, 3]")
            size().shouldBe(9L)
            hashCode().shouldBe(value.hashCode())

            equals(NullValue.INSTANCE).shouldBeFalse()
            equals(value).shouldBeFalse()
            equals(null).shouldBeFalse()
            equals("").shouldBeFalse()
            equals(StringValue.of("[1, 2, 3]")).shouldBeFalse()
        }
    }
}
