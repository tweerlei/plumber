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

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import de.tweerlei.plumber.worker.types.*

fun Map<String, AttributeValue>.fromDynamoDB() =
    mapValuesTo(Record()) { (_, v) ->
        when {
            v.isNULL == true -> NullValue.INSTANCE
            v.isBOOL != null -> BooleanValue.of(v.bool)
            v.n != null -> v.n.toLongOrNull()?.let { LongValue.of(it) } ?: DoubleValue.of(v.n.toDouble())
            // FIXME: there are more cases
            else -> StringValue.of(v.s)
        }
    }

fun Record.toDynamoDB() =
    mapValues { (_, v) ->
        when (v) {
            is NullValue -> AttributeValue().apply { `null` = true }
            is BooleanValue -> AttributeValue().apply { bool = v.toBoolean() }
            is LongValue -> AttributeValue().apply { n = v.toString() }
            is DoubleValue -> AttributeValue().apply { n = v.toString() }
            // FIXME: there are more cases
            else -> AttributeValue(v.toString())
        }
    }

fun Map<String, AttributeValue>.extractKey(partitionKey: String, rangeKey: String?) =
    filter { (k, _) -> k == partitionKey || k == rangeKey }
