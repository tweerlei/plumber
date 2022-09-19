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

class InstantValue(
    val value: Instant
): NumberValue {

    override val name = "instant"

    override fun toAny() =
        value
    override fun toBoolean() =
        toNumber() != 0L
    override fun toNumber() =
        value.toEpochMilli()
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.numberNode(toNumber().toLong())
    override fun toString() =
        value.toString()
    override fun equals(other: Any?) =
        other is Value && toNumber().toLong() == other.toNumber().toLong()
    override fun hashCode() =
        value.hashCode()
    override fun compareTo(other: ComparableValue) =
        toNumber().toLong().compareTo(other.toNumber().toLong())
}
