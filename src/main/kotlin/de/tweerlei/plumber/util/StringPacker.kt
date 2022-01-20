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

import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.min
import kotlin.math.pow

class StringPacker(
    charactersToUse: String,
    maxCharsToPack: Int = Int.MAX_VALUE
) {
    companion object {
        const val NO_CHAR = Character.MIN_VALUE
    }

    private val codeToChar: CharArray
    private val charToCode: IntArray
    private val maxChars: Int

    init {
        codeToChar = charactersToUse.plus(NO_CHAR).toSortedSet().toCharArray()
        charToCode = IntArray(codeToChar.last().code + 1)
        var index = -1
        var lastChar = NO_CHAR
        codeToChar.forEach { ch ->
            for (i in lastChar until ch)
                charToCode[i.code] = index
            lastChar = ch
            index++
        }
        charToCode[codeToChar.last().code] = index
        val maxCharsPerLong = 63 / ceil(log2(codeToChar.size.toDouble())).toInt()
        maxChars = min(maxCharsToPack, maxCharsPerLong)
    }

    fun pack(key: String, len: Int = maxChars): Long {
        var acc = 0L
        for (i in 0 until len) {
            if (i < key.length)
                acc = acc * codeToChar.size + charToCode(key[i])
            else
                acc *= codeToChar.size
        }
        return acc
    }

    fun maxValue(n: Int = maxChars) =
        codeToChar.size.toDouble().pow(n.toDouble()).toLong()

    fun unpack(packed: Long) =
        StringBuilder().apply {
            var remainder = packed
            while (remainder > 0L) {
                val ch = codeToChar(remainder)
                if (ch != NO_CHAR)
                    insert(0, ch)
                else if (isNotEmpty())
                    insert(0, codeToChar[1])
                remainder /= codeToChar.size
            }
        }.toString().let { key ->
            when {
                key.isEmpty() -> null
                else -> key
            }
        }

    private fun charToCode(ch: Char) =
        when {
            ch.code > charToCode.size -> codeToChar.size - 1
            else -> charToCode[ch.code]
        }

    private fun codeToChar(i: Long) =
        codeToChar[(i % codeToChar.size).toInt()]
}
