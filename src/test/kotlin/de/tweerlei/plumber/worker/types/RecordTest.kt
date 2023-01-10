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

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class RecordTest {

    @Test
    fun testNonEmpty() {
        with (Record.of(
            "foo" to StringValue.of("bar"),
            "0" to LongValue.of(42L),
        )) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBeSameInstanceAs(this)
            toBoolean().shouldBeTrue()
            toLong().shouldBe(2L)
            toDouble().shouldBe(2.0)
            toBigInteger().shouldBe(BigInteger.valueOf(2L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(2.0))
            with(toByteArray()) {
                size.shouldBe(15)
//                contentEquals(byteArrayOf(10, 0, 0, 0)).shouldBeTrue()
            }
            toRecord().shouldBeSameInstanceAs(this)
            with (toJsonNode()) {
                isObject.shouldBeTrue()
            }
            toString().shouldBe("{foo=bar, 0=42}")
            size().shouldBe(2L)
//            hashCode().shouldBe("hello".hashCode())
        }
    }

    @Test
    fun testEquals() {

        Record().shouldBeEmpty()

        Record.of(
            "foo" to StringValue.of("bar"),
            "0" to LongValue.of(42L),
        ).shouldBe(Record.of(
            "foo" to StringValue.of("bar"),
            "0" to LongValue.of(42L),
        ))

        Record.of(
            "foo" to StringValue.of("bar"),
            "0" to LongValue.of(42L),
        ).shouldBe(Record.of(
            "0" to LongValue.of(42L),
            "foo" to StringValue.of("bar"),
        ))
    }
}
