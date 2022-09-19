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

class Range(
    var startAfter: ComparableValue = NullValue.INSTANCE,
    var endWith: ComparableValue = NullValue.INSTANCE
): Value {

    companion object {
        fun from(startAfter: Any?, endWith: Any?) =
            Range(
                startAfter.toValue().toComparableValue(),
                endWith.toValue().toComparableValue()
            )
    }

    override val name = "range"

    override fun toAny() =
        this

    override fun toBoolean() =
        startAfter.toBoolean()

    override fun toNumber() =
        endWith.toNumber().toLong() - startAfter.toNumber().toLong()

    override fun toByteArray() =
        startAfter.toByteArray()

    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.arrayNode().apply {
            add(startAfter.toJsonNode())
            add(endWith.toJsonNode())
        }

    override fun size() =
        2L

    fun contains(value: ComparableValue) =
        when (value) {
            is NullValue -> false
            is DoubleValue -> contains(value.toNumber().toDouble(), startAfter.toNumberOrNull()?.toDouble(), endWith.toNumberOrNull()?.toDouble())
            is NumberValue -> contains(value.toNumber().toLong(), startAfter.toNumberOrNull()?.toLong(), endWith.toNumberOrNull()?.toLong())
            else -> contains(value.toString(), startAfter.toStringOrNull(), endWith.toStringOrNull())
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
                startAfter == other.startAfter &&
                endWith == other.endWith

    override fun hashCode() =
        startAfter.hashCode() xor endWith.hashCode()

    override fun toString() =
        "[${startAfter} .. ${endWith}]"

    override fun dump() =
        "[${startAfter.dump()} .. ${endWith.dump()}]"
}
