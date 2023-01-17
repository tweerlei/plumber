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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.bson.json.JsonObject
import java.math.BigDecimal
import java.math.BigInteger

class Node(
    private val value: JsonNode = JsonNodeFactory.instance.objectNode()
): Value {

    companion object {
        const val NAME = "node"
    }

    override fun getName() =
        NAME
    fun getValue(key: String) =
        value[key]?.let { Node(it) } ?: NullValue.INSTANCE

    override fun toAny() =
        value
    override fun toBoolean() =
        value.booleanValue()
    override fun toLong() =
        value.longValue()
    override fun toDouble() =
        value.doubleValue()
    override fun toBigInteger(): BigInteger =
        value.bigIntegerValue()
    override fun toBigDecimal(): BigDecimal =
        value.decimalValue()
    override fun toByteArray() =
        value.binaryValue() ?: byteArrayOf()
    override fun toJsonNode(): JsonNode =
        value

    override fun toRange() =
        when (value) {
            is ArrayNode -> if (value.size() >= 2)
                    Range(
                        value[0].toValue().toComparableValue(),
                        value[1].toValue().toComparableValue()
                    )
                else Range()
            else -> Range()
        }
    override fun toRecord() =
        when (value) {
            is ObjectNode -> Record().also { record ->
                value.fields().forEach { (k, v) -> record.setValue(k, Node(v)) }
            }
            is ArrayNode -> Record().also { record ->
                value.elements().asSequence().forEachIndexed { k, v -> record.setValue(k.toString(), Node(v)) }
            }
            else -> Record.of(this)
        }
    override fun toNode() =
        this

    override fun size() =
        value.size().toLong()
    override fun equals(other: Any?) =
        other is Value && value == other.toJsonNode()
    override fun hashCode() =
        value.hashCode()
    override fun toString() =
        value.toString()
}
