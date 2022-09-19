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

import de.tweerlei.plumber.util.range.NumericStringPacker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NumericStringPackerTest {

    @Test
    fun testPackNumeric() {
        val packer = NumericStringPacker("0123456789abcdef")

        assertEquals(0xaffe, packer.pack("affe"))
    }

    @Test
    fun testUnpackNumeric() {
        val packer = NumericStringPacker("0123456789abcdef")

        assertEquals("affe", packer.unpack(0xaffe))
    }
}
