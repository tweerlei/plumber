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
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException

fun Any?.coerceToString() =
    when (this) {
        null -> ""
        is ByteArray -> toString(StandardCharsets.UTF_8)
        else -> toString()
    }

fun Any?.coerceToLong() =
    when (this) {
        null -> 0L
        is Number -> toLong()
        is String -> toLongOrNull() ?: 0L
        is Instant -> toEpochMilli()
        is Duration -> toMillis()
        is ByteArray -> toLenientNumber().toLong()
        is Boolean -> if (this) 1L else 0L
        else -> toString().toLongOrNull() ?: 0L
    }

fun Any?.coerceToNumber() =
    when (this) {
        null -> 0
        is Number -> this
        is String -> toLenientNumber()
        is Instant -> toEpochMilli()
        is Duration -> toMillis()
        is ByteArray -> toLenientNumber()
        is Boolean -> if (this) 1 else 0
        else -> toString().toLenientNumber()
    }

fun Any?.coerceToBoolean() =
    when (this) {
        null -> false
        is Number -> toLong() != 0L
        is String -> toLenientBoolean()
        is Instant -> toEpochMilli() != 0L
        is Duration -> toMillis() != 0L
        is ByteArray -> toLenientNumber() != 0
        is Boolean -> this
        else -> toString().toLenientBoolean()
    }

fun Any?.coerceToInstant(): Instant =
    when (this) {
        null -> 0L.toInstant()
        is Number -> toLong().toInstant()
        is String -> toLenientInstant()
        is Instant -> this
        is Duration -> Instant.ofEpochMilli(toMillis())
        is ByteArray -> toLenientNumber().toLong().toInstant()
        is Boolean -> if (this) 1L.toInstant() else 0L.toInstant()
        else -> toString().toLenientInstant()
    }

fun Any?.coerceToDuration(): Duration =
    when (this) {
        null -> 0L.toDuration()
        is Number -> toLong().toDuration()
        is String -> toLenientDuration()
        is Instant -> Duration.ofMillis(toEpochMilli())
        is Duration -> this
        is ByteArray -> toLenientNumber().toLong().toDuration()
        is Boolean -> if (this) 1L.toDuration() else 0L.toDuration()
        else -> toString().toLenientDuration()
    }

fun Any?.coerceToComparable() =
    when (this) {
        is Comparable<*> -> this
        else -> this.coerceToString()
    }

fun Any?.coerceToJsonNode(): JsonNode =
    when (this) {
        null -> JsonNodeFactory.instance.nullNode()
        is JsonNode -> this
        is String -> JsonNodeFactory.instance.textNode(this)
        is Boolean -> JsonNodeFactory.instance.booleanNode(this)
        is Long -> JsonNodeFactory.instance.numberNode(this)
        is Double -> JsonNodeFactory.instance.numberNode(this)
        is ByteArray -> JsonNodeFactory.instance.binaryNode(this)
        is Instant -> JsonNodeFactory.instance.numberNode(this.coerceToLong())
        is Duration -> JsonNodeFactory.instance.numberNode(this.coerceToLong())
        is Map<*, *> -> JsonNodeFactory.instance.objectNode().also { node ->
            forEach { key, value -> node.set<JsonNode>(key.coerceToString(), value.coerceToJsonNode()) }
        }
        is Collection<*> -> JsonNodeFactory.instance.arrayNode().also { node ->
            forEach { value -> node.add(value.coerceToJsonNode()) }
        }
        else -> JsonNodeFactory.instance.textNode(this.coerceToString())
    }

fun Any?.coerceToByteArray() =
    when (this) {
        null -> byteArrayOf()
        is Number -> toLong().toByteArray()
        is String -> toByteArray(StandardCharsets.UTF_8)
        is Instant -> toEpochMilli().toByteArray()
        is Duration -> toMillis().toByteArray()
        is ByteArray -> this
        is Boolean -> if (this) byteArrayOf(1) else byteArrayOf(0)
        else -> toString().toByteArray(StandardCharsets.UTF_8)
    }

private fun ByteArray.toLenientNumber(): Number =
    when (size) {
        0 -> 0
        1 -> this[0]
        2,3 -> (this[0].toLong() and 0xff) +
                (this[1].toLong() and 0xff shl 8)
        4,5,6,7 -> (this[0].toLong() and 0xff) +
                (this[1].toLong() and 0xff shl 8) +
                (this[2].toLong() and 0xff shl 16) +
                (this[3].toLong() and 0xff shl 24)
        else -> (this[0].toLong() and 0xff) +
                (this[1].toLong() and 0xff shl 8) +
                (this[2].toLong() and 0xff shl 16) +
                (this[3].toLong() and 0xff shl 24) +
                (this[4].toLong() and 0xff shl 32) +
                (this[5].toLong() and 0xff shl 40) +
                (this[6].toLong() and 0xff shl 48) +
                (this[7].toLong() and 0xff shl 56)
    }

private fun Long.toByteArray() =
    byteArrayOf(
        (this and 0xff).toByte(),
        (this shr 8 and 0xff).toByte(),
        (this shr 16 and 0xff).toByte(),
        (this shr 24 and 0xff).toByte(),
        (this shr 32 and 0xff).toByte(),
        (this shr 40 and 0xff).toByte(),
        (this shr 48 and 0xff).toByte(),
        (this shr 56 and 0xff).toByte(),
    )

private fun String.toLenientNumber(): Number =
    toLongOrNull() ?: toDoubleOrNull() ?: 0L

private fun String.toLenientBoolean() =
    !(isNullOrBlank() || this == "0" || this == "false")

private fun String.toLenientInstant() =
    try {
        Instant.parse(this)
    } catch (e: DateTimeParseException) {
        toLenientNumber().toLong().toInstant()
    }

private fun String.toLenientDuration() =
    try {
        Duration.parse(this)
    } catch (e: DateTimeParseException) {
        toLenientNumber().toLong().toDuration()
    }

private fun Long.toInstant() =
    when {
        this < 10000000000 -> Instant.ofEpochSecond(this)
        else -> Instant.ofEpochMilli(this)
    }

private fun Long.toDuration() =
    when {
        this < 10000000000 -> Duration.ofSeconds(this)
        else -> Duration.ofMillis(this)
    }
