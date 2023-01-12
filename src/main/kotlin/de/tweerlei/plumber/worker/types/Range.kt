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
        const val NAME = "range"

        fun of(startAfter: Long?, endWith: Long?) =
            Range(
                startAfter?.let { LongValue.of(it) } ?: NullValue.INSTANCE,
                endWith?.let { LongValue.of(it) } ?: NullValue.INSTANCE
            )
        fun of(startAfter: String?, endWith: String?) =
            Range(
                startAfter?.let { StringValue.of(it) } ?: NullValue.INSTANCE,
                endWith?.let { StringValue.of(it) } ?: NullValue.INSTANCE
            )
    }

    override fun getName() =
        NAME

    override fun toAny() =
        this
    override fun toBoolean() =
        size() != 0L
    override fun toLong() =
        size()
    override fun toDouble() =
        size().toDouble()
    override fun toByteArray() =
        toString().toByteArray()
    override fun toJsonNode(): JsonNode =
        JsonNodeFactory.instance.arrayNode().apply {
            add(startAfter.toJsonNode())
            add(endWith.toJsonNode())
        }

    override fun toRange() =
        this
    override fun toRecord() =
        Record.of(startAfter, endWith)

    fun contains(value: ComparableValue) =
        when (value) {
            is NullValue -> false
            is DoubleValue -> contains(value.toDouble(), startAfter.asOptional()?.toDouble(), endWith.asOptional()?.toDouble())
            is NumberValue -> contains(value.toLong(), startAfter.asOptional()?.toLong(), endWith.asOptional()?.toLong())
            else -> contains(value.toString(), startAfter.asOptional()?.toString(), endWith.asOptional()?.toString())
        }

    private fun <T: Comparable<*>> contains(value: T, lower: Comparable<T>?, upper: Comparable<T>?) =
        when {
            lower != null && upper != null -> (lower < value && upper >= value) || (lower > value && upper <= value)
            lower != null && upper == null -> lower < value
            lower == null && upper != null -> upper >= value
            else -> true
        }

    override fun size() =
        endWith.toLong() - startAfter.toLong()
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
