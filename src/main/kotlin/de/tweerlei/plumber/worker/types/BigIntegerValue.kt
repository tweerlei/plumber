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
import java.math.BigInteger

class BigIntegerValue private constructor(
    private val value: BigInteger
): NumberValue {

    companion object {
        const val NAME = "bigint"

        fun of(value: BigInteger) =
            BigIntegerValue(value)
    }

    override fun getName() =
        NAME

    override fun toAny() =
        value
    override fun toBoolean() =
        value != BigInteger.ZERO
    override fun toLong() =
        value.toLong()
    override fun toDouble() =
        value.toDouble()
    override fun toBigInteger() =
        value
    override fun toBigDecimal() =
        value.toBigDecimal()
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.numberNode(value)

    override fun compareTo(other: ComparableValue) =
        value.compareTo(other.toBigInteger())
    override fun equals(other: Any?) =
        other is Value && value == other.toBigInteger()
    override fun hashCode() =
        value.hashCode()
    override fun toString() =
        value.toString()
}
