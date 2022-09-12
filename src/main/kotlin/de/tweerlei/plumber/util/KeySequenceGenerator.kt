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

class KeySequenceGenerator(
    charactersToUse: String? = null
) {

    companion object {
        // Safe characters, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-keys.html
        const val SAFE_CHARS = "!'()*-./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"
    }

    val packer = NumericStringPacker(charactersToUse ?: SAFE_CHARS)

    fun generateSequence(startAfter: String?, endWith: String?, increment: Long) =
        generateSequence(
            startAfter?.let { packer.pack(it) },
            endWith?.let { packer.pack(it) },
            increment
        )

    fun generateSequence(startAfter: Long?, endWith: Long?, increment: Long) =
        when {
            startAfter == null -> emptySequence()
            endWith == null -> {
                var lastValue = startAfter
                generateSequence {
                    lastValue += increment
                    packer.unpack(lastValue)
                }
            }
            else -> {
                var lastValue = startAfter
                generateSequence {
                    lastValue += increment
                    when {
                        increment > 0 && lastValue > endWith -> null
                        increment < 0 && lastValue < endWith -> null
                        else -> packer.unpack(lastValue)
                    }
                }
            }
        }
}
