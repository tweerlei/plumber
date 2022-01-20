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
package de.tweerlei.plumber.worker.dynamodb

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import de.tweerlei.plumber.worker.Record

fun Map<String, AttributeValue>.fromDynamoDB() =
    mapValuesTo(Record()) { (_, v) ->
        when {
            v.isNULL == true -> null
            v.isBOOL != null -> v.bool
            v.n != null -> v.n.toLongOrNull() ?: v.n.toDouble()
            // FIXME: there are more cases
            else -> v.s
        }
    }

fun Record.toDynamoDB() =
    mapValues { (_, v) ->
        when (v) {
            null -> AttributeValue().apply { `null` = true }
            is Boolean -> AttributeValue().apply { bool = v }
            is Number -> AttributeValue().apply { n = v.toString() }
            // FIXME: there are more cases
            else -> AttributeValue(v.toString())
        }
    }
