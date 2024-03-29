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
import java.time.Instant
import java.util.*

class InstantValue private constructor(
    private val value: Instant
): NumberValue {

    companion object {
        const val NAME = "instant"

        fun of(value: Instant) =
            InstantValue(value)
        fun of(value: Date) =
            InstantValue(value.toInstant())
        fun ofEpochMilli(value: Long) =
            InstantValue(Instant.ofEpochMilli(value))
    }

    override fun getName() =
        NAME

    override fun toAny() =
        value
    override fun toBoolean() =
        toLong() != 0L
    override fun toLong() =
        value.toEpochMilli()
    override fun toDouble() =
        toLong().toDouble()
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.numberNode(toLong())

    override fun compareTo(other: ComparableValue) =
        toLong().compareTo(other.toLong())
    override fun equals(other: Any?) =
        other is Value && toLong() == other.toLong()
    override fun hashCode() =
        value.hashCode()
    override fun toString() =
        value.toString()
}
