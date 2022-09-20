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

class BooleanValue private constructor(
    val value: Boolean
): ComparableValue {

    companion object {
        val TRUE = BooleanValue(true)
        val FALSE = BooleanValue(false)

        fun of(value: Boolean) =
            when (value) {
                true -> TRUE
                false -> FALSE
            }
    }

    override fun getName() =
        "boolean"

    override fun toAny() =
        value
    override fun toBoolean() =
        value
    override fun toNumber() =
        if (value) 1L else 0L
    override fun toByteArray() =
        byteArrayOf(toNumber().toByte())
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.booleanNode(value)
    override fun size() =
        1L
    override fun toString() =
        value.toString()
    override fun equals(other: Any?) =
        other is Value && value == other.toBoolean()
    override fun hashCode() =
        value.hashCode()
    override fun compareTo(other: ComparableValue) =
        other.toBoolean().let { otherValue ->
            when {
                value && !otherValue -> 1
                !value && otherValue -> -1
                else -> 0
            }
        }
}
