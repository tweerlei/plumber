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

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class NodeTest {

    @Test
    fun testNonEmpty() {
        val jsonNode = JsonNodeFactory.instance.objectNode()
        jsonNode.put("foo", "bar")
        jsonNode.put("0", 42L)

        with (Node(jsonNode)) {
            asOptional().shouldBeSameInstanceAs(this)
            toAny().shouldBeSameInstanceAs(jsonNode)
            toBoolean().shouldBeFalse()
            toLong().shouldBe(0L)
            toDouble().shouldBe(0.0)
            toBigInteger().shouldBe(BigInteger.valueOf(0L))
            toBigDecimal().shouldBe(BigDecimal.valueOf(0L))
            with(toByteArray()) {
                size.shouldBe(0)
//                contentEquals(byteArrayOf(10, 0, 0, 0)).shouldBeTrue()
            }
            with (toRecord()) {
                size().shouldBe(2)
                getValue("foo").toAny().shouldBe(JsonNodeFactory.instance.textNode("bar"))
                getValue("0").toAny().shouldBe(JsonNodeFactory.instance.numberNode(42L))
            }
            toJsonNode().shouldBeSameInstanceAs(jsonNode)
            toString().shouldBe("""{"foo":"bar","0":42}""")
            size().shouldBe(2L)
//            hashCode().shouldBe("hello".hashCode())
        }
    }

    @Test
    fun testEquals() {

        Node().shouldBe(Node(JsonNodeFactory.instance.objectNode()))

        val jsonNode = JsonNodeFactory.instance.objectNode()
        jsonNode.put("foo", "bar")
        jsonNode.put("0", 42L)

        Node(jsonNode).shouldBe(Node(jsonNode))
    }

    @Test
    fun testArrayToRecord() {
        val jsonNode = JsonNodeFactory.instance.arrayNode()
        jsonNode.add("bar")
        jsonNode.add(42L)

        with (Node(jsonNode)) {
            with (toRecord()) {
                size().shouldBe(2)
                getValue("0").toAny().shouldBe(JsonNodeFactory.instance.textNode("bar"))
                getValue("1").toAny().shouldBe(JsonNodeFactory.instance.numberNode(42L))
            }
        }
    }

    @Test
    fun testSimpleToRecord() {
        val jsonNode = JsonNodeFactory.instance.textNode("Hello")

        with (Node(jsonNode)) {
            with (toRecord()) {
                size().shouldBe(1)
                getValue("0").toAny().shouldBe(JsonNodeFactory.instance.textNode("Hello"))
            }
        }
    }
}
