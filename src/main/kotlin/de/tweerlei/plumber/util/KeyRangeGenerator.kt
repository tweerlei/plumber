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

class KeyRangeGenerator(
    charactersToUse: String
) {

    val packer = StringPacker(charactersToUse)

    fun generateRanges(numberOfPartitions: Int, startAfter: String?, endWith: String?) =
        when {
            startAfter == null -> createRange(numberOfPartitions, "", "", endWith)
            endWith == null -> createRange(numberOfPartitions, "", startAfter, null)
            startAfter >= endWith -> emptyList()
            else -> extractCommonPrefix(startAfter, endWith).let { commonPrefix ->
                createRange(
                    numberOfPartitions,
                    commonPrefix,
                    startAfter.substring(commonPrefix.length),
                    endWith.substring(commonPrefix.length)
                )
            }
        }

    private fun createRange(numberOfPartitions: Int, commonPrefix: String, startAfter: String, endWith: String?) =
        encodeInterval(numberOfPartitions, startAfter, endWith).let { range ->
            (1..numberOfPartitions).map { partition ->
                KeyRange(
                    generateKey(commonPrefix, startAfter, endWith, range, partition - 1, numberOfPartitions),
                    generateKey(commonPrefix, startAfter, endWith, range, partition, numberOfPartitions)
                )
            }
        }

    private fun encodeInterval(numberOfPartitions: Int, startAfter: String, endWith: String?): LongRange {
        var start = 0L
        var end = 0L
        var index = 1
        while (end - start < 3 * numberOfPartitions) {
            start = packer.pack(startAfter.take(index), index)
            end = when (endWith) {
                null -> packer.maxValue(index)
                else -> packer.pack(endWith.take(index), index)
            }
            index++
        }
        return when (endWith) {
            null -> LongRange(start, end - 1)
            else -> LongRange(start, end)
        }
    }

    private fun generateKey(commonPrefix: String, startAfter: String, endWith: String?, range: LongRange, numerator: Int, denominator: Int) =
        when (numerator) {
            0 -> startAfter
            denominator -> endWith
            else -> packer.unpack(range.first + numerator * (range.last - range.first + 1) / denominator)
        }?.let {
            commonPrefix + it
        }?.ifEmpty { null }
}
