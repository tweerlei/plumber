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
package de.tweerlei.plumber.util.range

class StringHistogram(
    maxBuckets: Int,
    private val packer: StringPacker,
    private val prefix: String
) {

    private val histogram = Histogram(maxBuckets)

    fun add(value: String) {
        histogram.add(packer.pack(value.substring(prefix.length)))
    }

    fun toRange() =
        histogram.toRange()

    fun toMap() =
        histogram.toMap().mapKeys { (key, _) ->
            prefix + (packer.unpack(key) ?: "")
        }

    fun count() =
        histogram.count()

    fun reallocs() =
        histogram.reallocs()
}
