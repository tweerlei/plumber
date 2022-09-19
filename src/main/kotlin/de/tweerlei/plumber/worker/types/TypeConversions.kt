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

private val byteCache = ValueCache<Byte, LongValue>("long") { LongValue(it.toLong()) }
private val charCache = ValueCache<Char, StringValue>("char") { StringValue(it.toString()) }
private val intCache = ValueCache<Int, LongValue>("int") { LongValue(it.toLong()) }
private val longCache = ValueCache<Long, LongValue>("long") { LongValue(it) }
private val bigIntegerCache = ValueCache<BigInteger, LongValue>("bigint") { LongValue(it.toLong()) }
private val floatCache = ValueCache<Float, DoubleValue>("float") { DoubleValue(it.toDouble()) }
private val doubleCache = ValueCache<Double, DoubleValue>("double") { DoubleValue(it) }
private val bigDecimalCache = ValueCache<BigDecimal, DoubleValue>("bigdec") { DoubleValue(it.toDouble()) }
private val stringCache = ValueCache<String, StringValue>("string", { it.length < 100 }) { StringValue(it) }
private val instantCache = ValueCache<Instant, InstantValue>("instant") { InstantValue(it) }
private val durationCache = ValueCache<Duration, DurationValue>("duration") { DurationValue(it) }
private val dateCache = ValueCache<Date, InstantValue>("date") { InstantValue(it.toInstant()) }

fun Any?.toValue(): Value =
    when (this) {
        null -> NullValue.INSTANCE
        is Value -> this
        is Boolean -> BooleanValue.of(this)
        is Byte -> byteCache.getOrCreateValue(this)
        is Char -> charCache.getOrCreateValue(this)
        is Int -> intCache.getOrCreateValue(this)
        is Long -> longCache.getOrCreateValue(this)
        is BigInteger -> bigIntegerCache.getOrCreateValue(this)
        is Float -> floatCache.getOrCreateValue(this)
        is Double -> doubleCache.getOrCreateValue(this)
        is BigDecimal -> bigDecimalCache.getOrCreateValue(this)
        is String -> stringCache.getOrCreateValue(this)
        is ByteArray -> ByteArrayValue(this)
        is Instant -> instantCache.getOrCreateValue(this)
        is Duration -> durationCache.getOrCreateValue(this)
        is Date -> dateCache.getOrCreateValue(this)
        is JsonNode -> JsonNodeValue(this)
        else -> OtherValue(this)
    }

fun Value.toComparableValue() =
    when (this) {
        is ComparableValue -> this
        else -> NullValue.INSTANCE
    }

fun String?.toComparableValue(): ComparableValue =
    if (this == null || this == "null") NullValue.INSTANCE
    else toBooleanStrictOrNull()?.let { BooleanValue.of(it) }
        ?: toInstantOrNull()?.let { InstantValue(it) }
        ?: toDurationOrNull()?.let { DurationValue(it) }
        ?: toLongOrNull()?.let { LongValue(it) }
        ?: toDoubleOrNull()?.let { DoubleValue(it) }
        ?: StringValue(this)

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
