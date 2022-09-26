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

import de.tweerlei.plumber.worker.types.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class WorkItemTest {

    @Test
    fun testGetSetMain() {
        val item = WorkItem.of(StringValue.of("Test"))
        item.get().shouldBe(StringValue.of("Test"))

        item.getAs<Value>().shouldBe(StringValue.of("Test"))
        item.getAs<ComparableValue>().shouldBe(StringValue.of("Test"))
        item.getAs<StringValue>().shouldBe(StringValue.of("Test"))
        item.set(LongValue.of(42))
        item.getAs<Value>().shouldBe(LongValue.of(42L))
        item.getAs<ComparableValue>().shouldBe(LongValue.of(42L))
        item.getAs<NumberValue>().shouldBe(LongValue.of(42L))
        item.getAs<LongValue>().shouldBe(LongValue.of(42L))

        shouldThrow<ClassCastException> { item.getAs<StringValue>() }

        item.getOptional().shouldBe(LongValue.of(42L))
        item.getOptionalAs<LongValue>().shouldBe(LongValue.of(42L))
        item.set(NullValue.INSTANCE)
        item.getOptional().shouldBeNull()
        item.getOptionalAs<LongValue>().shouldBeNull()
    }

    @Test
    fun testGetSetAlternate() {
        val item = WorkItem.of(StringValue.of(""))
        item.set(StringValue.of("Test 1"), "value1")
        item.set(StringValue.of("Test 2"), "value2")

        item.getAs<StringValue>().shouldBe(StringValue.of(""))
        item.getAs<StringValue>("value1").shouldBe(StringValue.of("Test 1"))
        item.getAs<StringValue>("value2").shouldBe(StringValue.of("Test 2"))

        item.getAs<NullValue>("value3").shouldBe(NullValue.INSTANCE)
    }
}
