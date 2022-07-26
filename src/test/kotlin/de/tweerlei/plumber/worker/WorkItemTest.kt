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
package de.tweerlei.plumber.worker

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class WorkItemTest {

    @Test
    fun testGetSetMain() {
        val item = WorkItem.of("Test")
        item.get().shouldBe("Test")

        item.getAs<Any>().shouldBe("Test")
        item.getAs<String>().shouldBe("Test")
        item.set(42)
        item.getAs<Int>().shouldBe(42)

        shouldThrow<ClassCastException> { item.getAs<Long>() }

        item.getOptional().shouldBe(42)
        item.getOptionalAs<Int>().shouldBe(42)
        item.set(null)
        item.getOptional().shouldBeNull()
    }

    @Test
    fun testGetSetAlternate() {
        val item = WorkItem.of("")
        item.set("Test 1", "value1")
        item.set("Test 2", "value2")

        item.getAs<String>().shouldBe("")
        item.getAs<String>("value1").shouldBe("Test 1")
        item.getAs<String>("value2").shouldBe("Test 2")

        shouldThrow<NoSuchElementException> { item.getAs<String>("value3") }
    }
}
