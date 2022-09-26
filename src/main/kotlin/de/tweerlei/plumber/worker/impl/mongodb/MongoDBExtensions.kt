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
package de.tweerlei.plumber.worker.impl.mongodb

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.tweerlei.plumber.worker.types.Node
import org.bson.Document

fun Document.fromMongoDB(objectMapper: ObjectMapper): Node =
    toBsonDocument().toJson()
        .let { json ->
            objectMapper.readValue(json, JsonNode::class.java)
        }.let { jsonNode ->
            Node(jsonNode)
        }

fun Node.toMongoDB(objectMapper: ObjectMapper): Document =
    objectMapper.writeValueAsString(value)
        .let { json ->
            Document.parse(json)
        }

fun Document.extractKey(primaryKey: String) =
    Document(primaryKey, toBsonDocument()[primaryKey])
