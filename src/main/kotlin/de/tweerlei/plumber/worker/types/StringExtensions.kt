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

import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException

fun String?.toComparable(): Comparable<*>? =
    if (this == null || this == "null") null
    else toBooleanStrictOrNull()
        ?: toInstantOrNull()
        ?: toDurationOrNull()
        ?: toLongOrNull()
        ?: toDoubleOrNull()
        ?: this

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
