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

class BigDecimalValue(
    val value: BigDecimal
): NumberValue {

    companion object {
        const val NAME = "bigdec"
    }

    override fun getName() =
        NAME

    override fun toAny() =
        value
    override fun toBoolean() =
        value != BigDecimal.ZERO
    override fun toLong() =
        value.toLong()
    override fun toDouble() =
        value.toDouble()
    override fun toBigInteger(): BigInteger =
        value.toBigInteger()
    override fun toBigDecimal() =
        value
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.numberNode(value)
    override fun toString() =
        value.toString()

    override fun equals(other: Any?) =
        other is Value && value == other.toBigDecimal()
    override fun hashCode() =
        value.hashCode()
    override fun compareTo(other: ComparableValue) =
        value.compareTo(other.toBigDecimal())
}
