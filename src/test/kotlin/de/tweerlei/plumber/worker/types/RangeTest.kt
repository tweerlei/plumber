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
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class RangeTest {

    @Test
    fun testEquals() {

        Range(null, null).shouldBe(Range(null, null))
        Range(1, null).shouldBe(Range(1, null))
        Range(null, 2).shouldBe(Range(null, 2))
        Range(1, 2).shouldBe(Range(1, 2))
        Range(1, 2).shouldNotBe(Range(1L, 2L))
        Range("1", "2").shouldBe(Range("1", "2"))
    }

    @Test
    fun testContains() {

        Range(null, null).contains(null).shouldBeFalse()
        Range(null, null).contains(42).shouldBeTrue()
        Range(null, null).contains("42").shouldBeTrue()

        Range(-10, 10).contains(null).shouldBeFalse()
        Range(-10, 10).contains(-10).shouldBeFalse()
        Range(-10, 10).contains(-9).shouldBeTrue()
        Range(-10, 10).contains(10).shouldBeTrue()
        Range(-10, 10).contains(11).shouldBeFalse()
        Range(-10, 10).contains("05").shouldBeTrue()
        Range(-10, 10).contains("xyz").shouldBeFalse()

        Range(10, -10).contains(null).shouldBeFalse()
        Range(10, -10).contains(10).shouldBeFalse()
        Range(10, -10).contains(9).shouldBeTrue()
        Range(10, -10).contains(-10).shouldBeTrue()
        Range(10, -10).contains(-11).shouldBeFalse()
        Range(10, -10).contains("05").shouldBeTrue()
        Range(10, -10).contains("xyz").shouldBeFalse()

        Range("a", "z").contains(null).shouldBeFalse()
        Range("a", "z").contains("a").shouldBeFalse()
        Range("a", "z").contains("aa").shouldBeTrue()
        Range("a", "z").contains("b").shouldBeTrue()
        Range("a", "z").contains("bb").shouldBeTrue()
        Range("a", "z").contains("z").shouldBeTrue()
        Range("a", "z").contains("zz").shouldBeFalse()
    }
}
