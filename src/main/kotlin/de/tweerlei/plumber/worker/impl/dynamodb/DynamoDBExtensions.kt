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
package de.tweerlei.plumber.worker.impl.dynamodb

import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.types.*
import java.nio.ByteBuffer

fun Map<String, AttributeValue>.fromDynamoDB(objectMapper: ObjectMapper) =
    mapValuesTo(Record()) { (_, v) ->
        ItemUtils.toSimpleValue<Any>(v).let { map ->
            when (map) {
                is String,  // v.s
                is Number,  // v.n
                is ByteArray, // v.b
                is Boolean, // v.bool
                null -> map.toValue()
                else -> objectMapper.valueToTree<JsonNode>(map)
                    .let { node -> Node(node) }
            }
        }
    }

fun Record.toDynamoDB(objectMapper: ObjectMapper) =
    mapValues { (_, v) ->
        when (v) {
            is StringValue, // v.s
            is LongValue,   // v.n
            is DoubleValue, // v.n
            is BigDecimalValue, // v.n
            is BigIntegerValue, // v.n
            is ByteArrayValue,  // v.b
            is BooleanValue,    // v.bool
            is NullValue -> v.toAny()
            else -> v.toJsonNode().let { node ->
                objectMapper.treeToValue(node, Any::class.java)
            }
        }.let { map ->
            ItemUtils.toAttributeValue(map)
        }
    }

fun Map<String, AttributeValue>.extractKey(partitionKey: String, rangeKey: String?) =
    filter { (k, _) -> k == partitionKey || k == rangeKey }
