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
import java.nio.charset.Charset
import java.util.*

class ByteArrayValue private constructor(
    val value: ByteArray
): ComparableValue {

    companion object {
        const val NAME = "bytes"

        val EMPTY = ByteArrayValue(byteArrayOf())

        fun of(value: ByteArray) =
            when {
                value.isEmpty() -> EMPTY
                else -> ByteArrayValue(value)
            }
    }

    override fun getName() =
        NAME

    override fun toAny() =
        value
    override fun toBoolean() =
        value.find { byte -> byte.toInt() != 0 } != null
    override fun toLong() =
        when (value.size) {
            0 -> 0L
            1 -> value[0].toLong()
            2,3 -> (value[0].toLong() and 0xff) +
                    (value[1].toLong() and 0xff shl 8)
            4,5,6,7 -> (value[0].toLong() and 0xff) +
                    (value[1].toLong() and 0xff shl 8) +
                    (value[2].toLong() and 0xff shl 16) +
                    (value[3].toLong() and 0xff shl 24)
            else -> (value[0].toLong() and 0xff) +
                    (value[1].toLong() and 0xff shl 8) +
                    (value[2].toLong() and 0xff shl 16) +
                    (value[3].toLong() and 0xff shl 24) +
                    (value[4].toLong() and 0xff shl 32) +
                    (value[5].toLong() and 0xff shl 40) +
                    (value[6].toLong() and 0xff shl 48) +
                    (value[7].toLong() and 0xff shl 56)
        }
    override fun toDouble() =
        toLong().toDouble()
    override fun toByteArray() =
        value
    override fun toRecord() =
        Record.of(this)
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.binaryNode(value)
    override fun size() =
        value.size.toLong()
    override fun toString() =
        value.toString(Charset.defaultCharset())

    override fun equals(other: Any?) =
        other is Value && value.contentEquals(other.toByteArray())
    override fun hashCode() =
        value.contentHashCode()
    override fun compareTo(other: ComparableValue) =
        Arrays.compare(value, other.toByteArray())
}
