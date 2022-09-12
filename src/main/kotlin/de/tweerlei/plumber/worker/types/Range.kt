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

import java.time.Instant

class Range(
    var startAfter: Comparable<*>? = null,
    var endWith: Comparable<*>? = null
) {
    fun contains(value: Any?) =
        when (value) {
            null -> false
            is Boolean -> contains(value, startAfter?.coerceToBoolean(), endWith?.coerceToBoolean())
            is Instant -> contains(value, startAfter?.coerceToInstant(), endWith?.coerceToInstant())
            is Long -> contains(value, startAfter?.coerceToLong(), endWith?.coerceToLong())
            is Number -> contains(value.toDouble(), startAfter?.coerceToNumber()?.toDouble(), endWith?.coerceToNumber()?.toDouble())
            else -> contains(value.coerceToString(), startAfter?.coerceToString(), endWith?.coerceToString())
        }

    private fun <T: Comparable<*>> contains(value: T, lower: Comparable<T>?, upper: Comparable<T>?) =
        when {
            lower != null && upper != null -> (lower < value && upper >= value) || (lower > value && upper <= value)
            lower != null && upper == null -> lower < value
            lower == null && upper != null -> upper >= value
            else -> true
        }

    override fun equals(other: Any?) =
        other is Range &&
                other.startAfter == startAfter &&
                other.endWith == endWith

    override fun hashCode() =
        (startAfter?.hashCode() ?: 0) xor (endWith?.hashCode() ?: 0)

    override fun toString() =
        "[${startAfter} .. ${endWith}]"
}
