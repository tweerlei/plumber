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

class AnyValue private constructor(
    private val value: Any
): Value {

    companion object {
        const val NAME = "any"

        fun of(value: Any) =
            AnyValue(value)
    }

    private var stringValue: StringValue? = null

    private fun toStringValue() =
        stringValue ?: StringValue.of(value.toString())
            .also { stringValue = it}

    override fun getName() =
        NAME

    override fun toAny(): Any =
        value
    override fun toBoolean() =
        toStringValue().toBoolean()
    override fun toLong() =
        toStringValue().toLong()
    override fun toDouble() =
        toStringValue().toDouble()
    override fun toBigInteger() =
        toStringValue().toBigInteger()
    override fun toBigDecimal() =
        toStringValue().toBigDecimal()
    override fun toByteArray() =
        toStringValue().toByteArray()
    override fun toJsonNode() =
        // TODO: use ObjectMapper.valueToTree()
        toStringValue().toJsonNode()

    override fun toRange() =
        Range()
    override fun toRecord() =
        Record.of(this)

    override fun size() =
        toStringValue().size()
    override fun equals(other: Any?) =
        other is Value && value == other.toAny()
    override fun hashCode(): Int =
        value.hashCode()
    override fun toString() =
        toStringValue().toString()
    override fun dump() =
        "${getName()}:${value::class.simpleName}:${toString()}"
}
