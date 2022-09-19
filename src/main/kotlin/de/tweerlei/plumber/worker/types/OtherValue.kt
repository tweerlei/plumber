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

class OtherValue(
    val value: Any
): Value {

    inline fun <reified T: Any> to() =
        value as T

    override val name = "other"

    override fun toAny(): Any =
        value
    override fun toBoolean(): Boolean =
        toString().let {
            it.isNotBlank() && it != "0" && it != "false"
        }
    override fun toNumber(): Number =
        toString().let {
            it.toLongOrNull() ?: it.toDoubleOrNull() ?: 0L
        }
    override fun toByteArray(): ByteArray =
        toString().let {
            it.toByteArray()
        }
    override fun toJsonNode(): JsonNode =
        toString().let {
            JsonNodeFactory.instance.textNode(it)
        }
    override fun size(): Long =
        toString().let {
            it.length.toLong()
        }
    override fun toString(): String =
        value.toString()
    override fun dump() =
        "$name:${value::class.simpleName}:${toString()}"
    override fun equals(other: Any?) =
        other is Value && value == other.toAny()
    override fun hashCode(): Int =
        value.hashCode()
}
