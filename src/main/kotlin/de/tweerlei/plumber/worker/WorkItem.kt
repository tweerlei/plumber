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
package de.tweerlei.plumber.worker

import java.nio.charset.StandardCharsets
import java.time.Instant

class WorkItem private constructor(): HashMap<String, Any>() {

    companion object {
        const val DEFAULT_KEY = ""

        fun of(value: Any?, vararg entries: Pair<String, Any?>) =
            WorkItem().also { item ->
                entries.forEach { (k, v) -> if (v != null) item[k] = v }
                item.set(value)
            }
    }

    inline fun <reified T> getAs(key: String = DEFAULT_KEY): T =
        getValue(key) as T

    inline fun <reified T> getOptional(key: String = DEFAULT_KEY): T? =
        this[key] as T?

    fun getString(key: String = DEFAULT_KEY) =
        this[key].let { value ->
            when (value) {
                null -> ""
                is ByteArray -> value.toString(StandardCharsets.UTF_8)
                else -> value.toString()
            }
        }

    fun getInt(key: String = DEFAULT_KEY) =
        this[key].let { value ->
            when (value) {
                null -> 0
                is Number -> value.toInt()
                is String -> value.toIntOrNull() ?: 0
                is Instant -> value.epochSecond.toInt()
                is ByteArray -> value.toLenientNumber().toInt()
                is Boolean -> if (value) 1 else 0
                else -> value.toString().toIntOrNull() ?: 0
            }
        }

    fun getLong(key: String = DEFAULT_KEY) =
        this[key].let { value ->
            when (value) {
                null -> 0L
                is Number -> value.toLong()
                is String -> value.toLongOrNull() ?: 0L
                is Instant -> value.toEpochMilli()
                is ByteArray -> value.toLenientNumber().toLong()
                is Boolean -> if (value) 1L else 0L
                else -> value.toString().toLongOrNull() ?: 0L
            }
        }

    fun getNumber(key: String = DEFAULT_KEY) =
        this[key].let { value ->
            when (value) {
                null -> 0
                is Number -> value
                is String -> value.toLenientNumber()
                is Instant -> value.toEpochMilli()
                is ByteArray -> value.toLenientNumber()
                is Boolean -> if (value) 1 else 0
                else -> value.toString().toLenientNumber()
            }
        }

    fun getBoolean(key: String = DEFAULT_KEY) =
        this[key].let { value ->
            when (value) {
                null -> false
                is Number -> value.toInt() != 0
                is String -> value.toLenientBoolean()
                is Instant -> true
                is ByteArray -> value.toLenientNumber() != 0
                is Boolean -> value
                else -> value.toString().toLenientBoolean()
            }
        }

    fun getInstant(key: String = DEFAULT_KEY): Instant =
        this[key].let { value ->
            when (value) {
                null -> Instant.ofEpochSecond(0)
                is Int -> Instant.ofEpochSecond(value.toLong())
                is Number -> Instant.ofEpochMilli(value.toLong())
                is String -> Instant.ofEpochMilli(value.toLenientNumber().toLong())
                is Instant -> value
                is ByteArray -> Instant.ofEpochMilli(value.toLenientNumber().toLong())
                is Boolean -> if (value) Instant.ofEpochSecond(1) else Instant.ofEpochSecond(0)
                else -> Instant.ofEpochMilli(value.toString().toLenientNumber().toLong())
            }
        }

    fun getByteArray(key: String = DEFAULT_KEY) =
        this[key].let { value ->
            when (value) {
                null -> byteArrayOf()
                is Int -> value.toByteArray()
                is Number -> value.toLong().toByteArray()
                is String -> value.toByteArray(StandardCharsets.UTF_8)
                is Instant -> value.toEpochMilli().toByteArray()
                is ByteArray -> value
                is Boolean -> if (value) byteArrayOf(1) else byteArrayOf(0)
                else -> value.toString().toByteArray(StandardCharsets.UTF_8)
            }
        }

    fun set(value: Any?, key: String = DEFAULT_KEY) {
        when (value) {
            null -> remove(key)
            else -> put(key, value)
        }
    }

    fun setString(value: String, key: String = DEFAULT_KEY) {
        put(key, value.toAny())
    }

    fun plus(item: WorkItem) =
        WorkItem().also { newItem ->
            newItem.putAll(this)
            newItem.putAll(item)
        }

    private fun String.toAny(): Any =
        toBooleanStrictOrNull()
            ?: toLongOrNull()
            ?: toDoubleOrNull()
            ?: this

    private fun String.toLenientNumber(): Number =
        toLongOrNull() ?: toDoubleOrNull() ?: 0L

    private fun String.toLenientBoolean() =
        !(isNullOrBlank() || this == "0" || this == "false")

    private fun ByteArray.toLenientNumber(): Number =
        when (size) {
            0 -> 0
            1 -> this[0]
            2,3 -> (this[0].toInt() and 0xff) +
                    (this[1].toInt() and 0xff shl 8)
            4,5,6,7 -> (this[0].toInt() and 0xff) +
                    (this[1].toInt() and 0xff shl 8) +
                    (this[2].toInt() and 0xff shl 16) +
                    (this[3].toInt() and 0xff shl 24)
            else -> (this[0].toLong() and 0xff) +
                    (this[1].toLong() and 0xff shl 8) +
                    (this[2].toLong() and 0xff shl 16) +
                    (this[3].toLong() and 0xff shl 24) +
                    (this[4].toLong() and 0xff shl 32) +
                    (this[5].toLong() and 0xff shl 40) +
                    (this[6].toLong() and 0xff shl 48) +
                    (this[7].toLong() and 0xff shl 56)
        }

    private fun Int.toByteArray() =
        byteArrayOf(
            (this and 0xff).toByte(),
            (this shr 8 and 0xff).toByte(),
            (this shr 16 and 0xff).toByte(),
            (this shr 24 and 0xff).toByte(),
        )

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
}
