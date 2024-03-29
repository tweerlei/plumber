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
package de.tweerlei.plumber.util

import java.time.Duration

class Stopwatch {

    private val startTime = System.currentTimeMillis()

    fun elapsedMillis() =
        System.currentTimeMillis() - startTime

    fun elapsedDuration(): Duration =
        Duration.ofMillis(elapsedMillis())

    fun itemsPerSecond(count: Double) =
        (count * 1000.0) / elapsedMillis().coerceAtLeast(1L)

    fun millisPerItem(count: Double) =
        elapsedMillis() / count.coerceAtLeast(1.0)
}
