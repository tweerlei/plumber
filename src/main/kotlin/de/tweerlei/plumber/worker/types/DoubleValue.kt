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
import kotlin.math.round

class DoubleValue private constructor(
    private val value: Double
): NumberValue {

    companion object {
        const val NAME = "double"

        val ZERO = DoubleValue(0.0)
        val POSITIVE_INFINITY = DoubleValue(Double.POSITIVE_INFINITY)
        val NEGATIVE_INFINITY = DoubleValue(Double.NEGATIVE_INFINITY)
        val NAN = DoubleValue(Double.NaN)

        private const val PRECISION = 1_000_000_000_000.0

        fun of(value: Double) =
            when (value) {
                0.0 -> ZERO
                else -> DoubleValue(value)
            }
        fun of(value: Float) =
            when (value) {
                0f -> ZERO
                else -> DoubleValue(value.toDouble())
            }
        fun ofRounded(value: Double) =
            when {
                value.isFinite() -> round(value *  PRECISION).div(PRECISION)
                else -> value
            }.let { of(it)}
    }

    override fun getName() =
        NAME

    override fun toAny() =
        value
    override fun toBoolean() =
        !value.isNaN() && value != 0.0
    override fun toLong() =
        value.toLong()
    override fun toDouble() =
        value
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.numberNode(value)

    override fun compareTo(other: ComparableValue) =
        value.compareTo(other.toDouble())
    override fun equals(other: Any?) =
        other is Value && value == other.toDouble()
    override fun hashCode() =
        value.hashCode()
    override fun toString() =
        value.toString()
}
