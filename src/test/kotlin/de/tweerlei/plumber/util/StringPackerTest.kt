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

import de.tweerlei.plumber.util.range.StringPacker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringPackerTest {

    companion object {
        const val pow0 = 1L
        const val pow1 = 11L
        const val pow2 = 11L * 11L
        const val pow3 = 11L * 11L * 11L
        const val pow4 = 11L * 11L * 11L * 11L
        const val pow5 = 11L * 11L * 11L * 11L * 11L
        const val pow6 = 11L * 11L * 11L * 11L * 11L *
                11L
        const val pow7 = 11L * 11L * 11L * 11L * 11L *
                11L * 11L
        const val pow8 = 11L * 11L * 11L * 11L * 11L *
                11L * 11L * 11L
        const val pow9 = 11L * 11L * 11L * 11L * 11L *
                11L * 11L * 11L * 11L
        const val pow10 = 11L * 11L * 11L * 11L * 11L *
                11L * 11L * 11L * 11L * 11L
        const val pow11 = 11L * 11L * 11L * 11L * 11L *
                11L * 11L * 11L * 11L * 11L *
                11L
        const val pow12 = 11L * 11L * 11L * 11L * 11L *
                11L * 11L * 11L * 11L * 11L *
                11L * 11L
        const val pow13 = 11L * 11L * 11L * 11L * 11L *
                11L * 11L * 11L * 11L * 11L *
                11L * 11L * 11L
        const val pow14 = 11L * 11L * 11L * 11L * 11L *
            11L * 11L * 11L * 11L * 11L *
            11L * 11L * 11L * 11L
    }

    @Test
    fun testPack() {

        val packer = StringPacker("0123456789")

        assertEquals(0, packer.pack(""))

        assertEquals(0 * pow14, packer.pack("!"))
        assertEquals(1 * pow14, packer.pack("0"))
        assertEquals(2 * pow14, packer.pack("1"))
        assertEquals(3 * pow14, packer.pack("2"))
        assertEquals(4 * pow14, packer.pack("3"))
        assertEquals(5 * pow14, packer.pack("4"))
        assertEquals(6 * pow14, packer.pack("5"))
        assertEquals(7 * pow14, packer.pack("6"))
        assertEquals(8 * pow14, packer.pack("7"))
        assertEquals(9 * pow14, packer.pack("8"))
        assertEquals(10 * pow14, packer.pack("9"))
        assertEquals(10 * pow14, packer.pack("A"))

        assertEquals(0 * pow14 + 10 * pow13, packer.pack("!9"))
        assertEquals(10 * pow14 + 0 * pow13, packer.pack("9!"))

        assertEquals(1, packer.pack("!!!!!!!!!!!!!!0"))
        assertEquals(10, packer.pack("!!!!!!!!!!!!!!9"))
        assertEquals(11, packer.pack("!!!!!!!!!!!!!0"))
        assertEquals(12, packer.pack("!!!!!!!!!!!!!00"))

        assertEquals(4 * pow14
                + 3 * pow13,
            packer.pack("32"))
        assertEquals(4 * pow14
                + 3 * pow13
                + 2 * pow12,
            packer.pack("321"))
        assertEquals(4 * pow14
                + 3 * pow13
                + 2 * pow12
                + 1 * pow11
                + 10 * pow10
                + 9 * pow9
                + 8 * pow8
                + 7 * pow7
                + 6 * pow6
                + 5 * pow5
                + 4 * pow4
                + 3 * pow3
                + 2 * pow2
                + 1 * pow1
                + 10 * pow0,
            packer.pack("321098765432109"))
        assertEquals(4 * pow14
                + 3 * pow13
                + 2 * pow12
                + 1 * pow11
                + 10 * pow10
                + 9 * pow9
                + 8 * pow8
                + 7 * pow7
                + 6 * pow6
                + 5 * pow5
                + 4 * pow4
                + 3 * pow3
                + 2 * pow2
                + 1 * pow1
                + 10 * pow0,
            packer.pack("3210987654321098"))

        assertEquals(4 * pow14
                + 3 * pow13
                + 2 * pow12
                + 1 * pow11
                + 10 * pow10
                + 9 * pow9
                + 8 * pow8
                + 0 * pow7
                + 6 * pow6
                + 5 * pow5
                + 4 * pow4
                + 3 * pow3
                + 2 * pow2
                + 1 * pow1
                + 10 * pow0,
            packer.pack("3210987!5432109"))
    }

    @Test
    fun testPackShort() {

        val packer = StringPacker("0123456789", 2)

        assertEquals(0, packer.pack(""))

        assertEquals(0 * pow1, packer.pack("!"))
        assertEquals(1 * pow1, packer.pack("0"))
        assertEquals(2 * pow1, packer.pack("1"))
        assertEquals(3 * pow1, packer.pack("2"))
        assertEquals(4 * pow1, packer.pack("3"))
        assertEquals(5 * pow1, packer.pack("4"))
        assertEquals(6 * pow1, packer.pack("5"))
        assertEquals(7 * pow1, packer.pack("6"))
        assertEquals(8 * pow1, packer.pack("7"))
        assertEquals(9 * pow1, packer.pack("8"))
        assertEquals(10 * pow1, packer.pack("9"))
        assertEquals(10 * pow1, packer.pack("A"))
    }

    @Test
    fun testUnpack() {
        val packer = StringPacker("0123456789")

        assertEquals(null, packer.unpack(0))
        assertEquals("0", packer.unpack(1))
        assertEquals("9", packer.unpack(10))
        assertEquals("0", packer.unpack(11))
        assertEquals("00", packer.unpack(12))
        assertEquals("01", packer.unpack(13))

        assertEquals("0", packer.unpack(1 * pow14))
        assertEquals("1", packer.unpack(2 * pow14))
        assertEquals("2", packer.unpack(3 * pow14))
        assertEquals("3", packer.unpack(4 * pow14))
        assertEquals("4", packer.unpack(5 * pow14))
        assertEquals("5", packer.unpack(6 * pow14))
        assertEquals("6", packer.unpack(7 * pow14))
        assertEquals("7", packer.unpack(8 * pow14))
        assertEquals("8", packer.unpack(9 * pow14))
        assertEquals("9", packer.unpack(10 * pow14))

        assertEquals("9", packer.unpack(0 * pow14 + 10 * pow13))
        assertEquals("9", packer.unpack(10 * pow14 + 0 * pow13))

        assertEquals("12", packer.unpack(2 * pow14
                + 3 * pow13
        ))
        assertEquals("123", packer.unpack(2 * pow14
                + 3 * pow13
                + 4 * pow12
        ))
        assertEquals("321098765432109", packer.unpack(4 * pow14
                + 3 * pow13
                + 2 * pow12
                + 1 * pow11
                + 10 * pow10
                + 9 * pow9
                + 8 * pow8
                + 7 * pow7
                + 6 * pow6
                + 5 * pow5
                + 4 * pow4
                + 3 * pow3
                + 2 * pow2
                + 1 * pow1
                + 10 * pow0
        ))

        assertEquals("321098705432109", packer.unpack(4 * pow14
                + 3 * pow13
                + 2 * pow12
                + 1 * pow11
                + 10 * pow10
                + 9 * pow9
                + 8 * pow8
                + 0 * pow7
                + 6 * pow6
                + 5 * pow5
                + 4 * pow4
                + 3 * pow3
                + 2 * pow2
                + 1 * pow1
                + 10 * pow0
        ))
    }

    @Test
    fun testMaxValue() {
        val packer = StringPacker("0123456789")

        assertEquals(11, packer.maxValue(1))
        assertEquals(11 * pow14, packer.maxValue(15))
    }
}
