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
import java.math.BigDecimal
import java.math.BigInteger

class NullValueTest {

    @Test
    fun testIt() {
        with (NullValue.INSTANCE) {
            asOptional().shouldBeNull()
            toAny().shouldBeNull()
            toBoolean().shouldBeFalse()
            toLong().shouldBe(0L)
            toDouble().shouldBe(0.0)
            toBigInteger().shouldBe(BigInteger.valueOf(0L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(0.0))
            toByteArray().size.shouldBe(0)
            with (toRecord()) {
                size.shouldBe(1)
                getValue("0").toAny().shouldBeNull()
            }
            toJsonNode().isNull.shouldBeTrue()
            toString().shouldBe("")
            size().shouldBe(0L)
            hashCode().shouldBe(0)

            equals(NullValue.INSTANCE).shouldBeTrue()
            equals(null).shouldBeFalse()
            equals("").shouldBeFalse()
            equals(StringValue.of("")).shouldBeFalse()

            compareTo(NullValue.INSTANCE).shouldBe(0)
            compareTo(StringValue.of("")).shouldBe(-1)
            compareTo(LongValue.of(0L)).shouldBe(-1)
        }
    }
}
