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
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import java.math.BigDecimal
import java.math.BigInteger

class StringValue private constructor(
    val value: String
): ComparableValue {

    companion object {
        const val NAME = "string"

        val EMPTY = StringValue("")

        fun of(value: String) =
            when (value.isEmpty()) {
                true -> EMPTY
                else -> StringValue(value)
            }
        fun of(value: Char) =
            StringValue(value.toString())
    }

    override fun getName() =
        NAME

    override fun toAny() =
        value
    override fun toBoolean() =
        value.isNotBlank() && value != "0" && value != "false"
    override fun toLong(): Long =
        value.toLongOrNull() ?: 0L
    override fun toDouble(): Double =
        value.toDoubleOrNull() ?: 0.0
    override fun toBigInteger(): BigInteger =
        value.toBigIntegerOrNull() ?: BigInteger.valueOf(0L)
    override fun toBigDecimal(): BigDecimal =
        value.toBigDecimalOrNull() ?: BigDecimal.valueOf(0.0)
    override fun toByteArray() =
        value.toByteArray()
    override fun toRecord() =
        Record.of(this)
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.textNode(value)
    override fun size() =
        value.length.toLong()
    override fun toString() =
        value

    override fun equals(other: Any?) =
        other is Value && value == other.toString()
    override fun hashCode() =
        value.hashCode()
    override fun compareTo(other: ComparableValue) =
        value.compareTo(other.toString())
}
