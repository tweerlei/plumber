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

import de.tweerlei.plumber.worker.*
import de.tweerlei.plumber.worker.impl.ifEmptyGetFrom
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant

class StringExtensionsTest {

    @Test
    fun testToComparable() {
        null.toComparable().shouldBeNull()
        "null".toComparable().shouldBeNull()
        "true".toComparable().shouldBe(true)
        "false".toComparable().shouldBe(false)
        "0".toComparable().shouldBe(0L)
        "1".toComparable().shouldBe(1L)
        "-1".toComparable().shouldBe(-1L)
        "0.0".toComparable().shouldBe(0.0)
        "3.1415".toComparable().shouldBe(3.1415)
        "-3.1415".toComparable().shouldBe(-3.1415)
        "2e".toComparable().shouldBe("2e")
        "2e7".toComparable().shouldBe(20000000.0)
        "2022-02-27T00:00:00Z".toComparable().shouldBe(Instant.ofEpochSecond(1645920000))
    }

    @Test
    fun testIfEmptyGetFrom() {
        val item = WorkItem.of(42, "foo" to "bar")

        "Test".ifEmptyGetFrom(item).shouldBe("Test")
        "".ifEmptyGetFrom(item).shouldBe("42")
        "".ifEmptyGetFrom(item, "foo").shouldBe("bar")
    }
}
