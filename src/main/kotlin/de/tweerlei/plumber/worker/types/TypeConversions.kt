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
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.*

fun Any?.toValue(): Value =
    when (this) {
        null -> NullValue.INSTANCE
        is Value -> this
        is Boolean -> BooleanValue.of(this)
        is Byte -> LongValue.of(this)
        is Char -> StringValue.of(this)
        is Short -> LongValue.of(this)
        is Int -> LongValue.of(this)
        is Long -> LongValue.of(this)
        is BigInteger -> BigIntegerValue.of(this)
        is Float -> DoubleValue.of(this)
        is Double -> DoubleValue.of(this)
        is BigDecimal -> BigDecimalValue.of(this)
        is String -> StringValue.of(this)
        is ByteArray -> ByteArrayValue.of(this)
        is Instant -> InstantValue.of(this)
        is Duration -> DurationValue.of(this)
        is Date -> InstantValue.of(this)
        is JsonNode -> Node(this)
        is Collection<*> -> Record.ofCollection(this)
        is Map<*, *> -> Record.ofMap(this)
        else -> AnyValue.of(this)
    }

fun Value.toComparableValue() =
    when (this) {
        is ComparableValue -> this
        else -> NullValue.INSTANCE
    }

fun String?.toComparableValue(): ComparableValue =
    if (this == null || this == "null") NullValue.INSTANCE
    else toBooleanStrictOrNull()?.let { BooleanValue.of(it) }
        ?: toInstantOrNull()?.let { InstantValue.of(it) }
        ?: toDurationOrNull()?.let { DurationValue.of(it) }
        ?: toLongOrNull()?.let { LongValue.of(it) }
        ?: toDoubleOrNull()?.let { DoubleValue.of(it) }
        ?: toBigIntegerOrNull()?.let { BigIntegerValue.of(it) }
        ?: toBigDecimalOrNull()?.let { BigDecimalValue.of(it) }
        ?: StringValue.of(this)

private fun String.toInstantOrNull() =
    try {
        Instant.parse(this)
    } catch (e: DateTimeParseException) {
        null
    }

private fun String.toDurationOrNull() =
    try {
        Duration.parse(this)
    } catch (e: DateTimeParseException) {
        null
    }
