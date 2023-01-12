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

interface Value {

    fun getName(): String
    fun asOptional(): Value? =
        this

    fun toAny(): Any?
    fun toBoolean(): Boolean
    fun toLong(): Long
    fun toDouble(): Double
    fun toBigInteger(): BigInteger =
        toLong().toBigInteger()
    fun toBigDecimal(): BigDecimal =
        toDouble().toBigDecimal()
    fun toInstant(): Instant =
        Instant.ofEpochMilli(toLong())
    fun toDuration(): Duration =
        Duration.ofMillis(toLong())
    fun toByteArray(): ByteArray
    fun toJsonNode(): JsonNode

    fun toRange(): Range
    fun toRecord(): Record
    fun toNode(): Node =
        Node(toJsonNode())

    fun size(): Long
    fun dump() =
        "${getName()}:${toString()}"
}
