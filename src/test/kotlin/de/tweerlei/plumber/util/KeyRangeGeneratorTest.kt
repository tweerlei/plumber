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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KeyRangeGeneratorTest {

    @Test
    fun `When neither start or end are given, create a single range`() {
        val result = KeyRangeGenerator().generateRanges(1,null, null)

        assertEquals(1, result.size)
        assertEquals(KeyRange(null, null), result[0])
    }

    @Test
    fun `When neither start or end are given, create multiple ranges`() {
        val result = KeyRangeGenerator().generateRanges(16,null, null)

        assertEquals(16, result.size)
        assertEquals(KeyRange(null, ")"), result[0])
        assertEquals(KeyRange(")", "0"), result[1])
        assertEquals(KeyRange("0", "4"), result[2])
        assertEquals(KeyRange("4", "9"), result[3])
        assertEquals(KeyRange("9", "D"), result[4])
        assertEquals(KeyRange("D", "I"), result[5])
        assertEquals(KeyRange("I", "M"), result[6])
        assertEquals(KeyRange("M", "R"), result[7])
        assertEquals(KeyRange("R", "V"), result[8])
        assertEquals(KeyRange("V", "_"), result[9])
        assertEquals(KeyRange("_", "d"), result[10])
        assertEquals(KeyRange("d", "i"), result[11])
        assertEquals(KeyRange("i", "m"), result[12])
        assertEquals(KeyRange("m", "r"), result[13])
        assertEquals(KeyRange("r", "v"), result[14])
        assertEquals(KeyRange("v", null), result[15])
    }

    @Test
    fun `When neither start or end are given, create multiple ranges for custom character set`() {
        val result = KeyRangeGenerator("0123456789abcdef").generateRanges(16,null, null)

        assertEquals(16, result.size)
        assertEquals(KeyRange(null, "00"), result[0])
        assertEquals(KeyRange("00", "11"), result[1])
        assertEquals(KeyRange("11", "22"), result[2])
        assertEquals(KeyRange("22", "33"), result[3])
        assertEquals(KeyRange("33", "44"), result[4])
        assertEquals(KeyRange("44", "55"), result[5])
        assertEquals(KeyRange("55", "66"), result[6])
        assertEquals(KeyRange("66", "77"), result[7])
        assertEquals(KeyRange("77", "88"), result[8])
        assertEquals(KeyRange("88", "99"), result[9])
        assertEquals(KeyRange("99", "aa"), result[10])
        assertEquals(KeyRange("aa", "bb"), result[11])
        assertEquals(KeyRange("bb", "cc"), result[12])
        assertEquals(KeyRange("cc", "dd"), result[13])
        assertEquals(KeyRange("dd", "ee"), result[14])
        assertEquals(KeyRange("ee", null), result[15])
    }

    @Test
    fun `When only start given, create a single range`() {
        val result = KeyRangeGenerator().generateRanges(1,"abc", null)

        assertEquals(1, result.size)
        assertEquals(KeyRange("abc", null), result[0])
    }

    @Test
    fun `When only start given, create multiple ranges`() {
        val result = KeyRangeGenerator().generateRanges(16,"abc", null)

        assertEquals(16, result.size)
        assertEquals(KeyRange("abc", "c8"), result[0])
        assertEquals(KeyRange("c8", "dn"), result[1])
        assertEquals(KeyRange("dn", "fK"), result[2])
        assertEquals(KeyRange("fK", "gz"), result[3])
        assertEquals(KeyRange("gz", "iW"), result[4])
        assertEquals(KeyRange("iW", "k2"), result[5])
        assertEquals(KeyRange("k2", "lh"), result[6])
        assertEquals(KeyRange("lh", "nE"), result[7])
        assertEquals(KeyRange("nE", "ot"), result[8])
        assertEquals(KeyRange("ot", "qQ"), result[9])
        assertEquals(KeyRange("qQ", "s*"), result[10])
        assertEquals(KeyRange("s*", "tb"), result[11])
        assertEquals(KeyRange("tb", "v8"), result[12])
        assertEquals(KeyRange("v8", "wn"), result[13])
        assertEquals(KeyRange("wn", "yK"), result[14])
        assertEquals(KeyRange("yK", null), result[15])
    }

    @Test
    fun `When only end given, create a single range`() {
        val result = KeyRangeGenerator().generateRanges(1,null, "ABC")

        assertEquals(1, result.size)
        assertEquals(KeyRange(null, "ABC"), result[0])
    }

    @Test
    fun `When only end given, create multiple ranges`() {
        val result = KeyRangeGenerator().generateRanges(16,null, "ABC")

        assertEquals(16, result.size)
        assertEquals(KeyRange(null, "!5"), result[0])
        assertEquals(KeyRange("!5", "'K"), result[1])
        assertEquals(KeyRange("'K", "(Z"), result[2])
        assertEquals(KeyRange("(Z", ")n"), result[3])
        assertEquals(KeyRange(")n", "-'"), result[4])
        assertEquals(KeyRange("-'", ".7"), result[5])
        assertEquals(KeyRange(".7", "/M"), result[6])
        assertEquals(KeyRange("/M", "0a"), result[7])
        assertEquals(KeyRange("0a", "1p"), result[8])
        assertEquals(KeyRange("1p", "3)"), result[9])
        assertEquals(KeyRange("3)", "49"), result[10])
        assertEquals(KeyRange("49", "5O"), result[11])
        assertEquals(KeyRange("5O", "6c"), result[12])
        assertEquals(KeyRange("6c", "7r"), result[13])
        assertEquals(KeyRange("7r", "9-"), result[14])
        assertEquals(KeyRange("9-", "ABC"), result[15])
    }

    @Test
    fun `When start and end given, create a single range`() {
        val result = KeyRangeGenerator().generateRanges(1,"012", "abc")

        assertEquals(1, result.size)
        assertEquals(KeyRange("012", "abc"), result[0])
    }

    @Test
    fun `When start and end given, create multiple ranges`() {
        val result = KeyRangeGenerator().generateRanges(16,"012", "abc")

        assertEquals(16, result.size)
        assertEquals(KeyRange("012", "2P"), result[0])
        assertEquals(KeyRange("2P", "4n"), result[1])
        assertEquals(KeyRange("4n", "73"), result[2])
        assertEquals(KeyRange("73", "9S"), result[3])
        assertEquals(KeyRange("9S", "Bq"), result[4])
        assertEquals(KeyRange("Bq", "E6"), result[5])
        assertEquals(KeyRange("E6", "GV"), result[6])
        assertEquals(KeyRange("GV", "It"), result[7])
        assertEquals(KeyRange("It", "L8"), result[8])
        assertEquals(KeyRange("L8", "NX"), result[9])
        assertEquals(KeyRange("NX", "Pv"), result[10])
        assertEquals(KeyRange("Pv", "SB"), result[11])
        assertEquals(KeyRange("SB", "U_"), result[12])
        assertEquals(KeyRange("U_", "Wy"), result[13])
        assertEquals(KeyRange("Wy", "ZE"), result[14])
        assertEquals(KeyRange("ZE", "abc"), result[15])
    }

    @Test
    fun `When start and end given, create multiple ranges with common prefix`() {
        val result = KeyRangeGenerator().generateRanges(16,"Test012", "Testabc")

        assertEquals(16, result.size)
        assertEquals(KeyRange("Test012", "Test2P"), result[0])
        assertEquals(KeyRange("Test2P", "Test4n"), result[1])
        assertEquals(KeyRange("Test4n", "Test73"), result[2])
        assertEquals(KeyRange("Test73", "Test9S"), result[3])
        assertEquals(KeyRange("Test9S", "TestBq"), result[4])
        assertEquals(KeyRange("TestBq", "TestE6"), result[5])
        assertEquals(KeyRange("TestE6", "TestGV"), result[6])
        assertEquals(KeyRange("TestGV", "TestIt"), result[7])
        assertEquals(KeyRange("TestIt", "TestL8"), result[8])
        assertEquals(KeyRange("TestL8", "TestNX"), result[9])
        assertEquals(KeyRange("TestNX", "TestPv"), result[10])
        assertEquals(KeyRange("TestPv", "TestSB"), result[11])
        assertEquals(KeyRange("TestSB", "TestU_"), result[12])
        assertEquals(KeyRange("TestU_", "TestWy"), result[13])
        assertEquals(KeyRange("TestWy", "TestZE"), result[14])
        assertEquals(KeyRange("TestZE", "Testabc"), result[15])
    }

    @Test
    fun `When start and end given, create exact ranges`() {
        val result = KeyRangeGenerator().generateRanges(16,"0", "G")

        assertEquals(16, result.size)
        assertEquals(KeyRange("0", "1"), result[0])
        assertEquals(KeyRange("1", "2"), result[1])
        assertEquals(KeyRange("2", "3"), result[2])
        assertEquals(KeyRange("3", "4"), result[3])
        assertEquals(KeyRange("4", "5"), result[4])
        assertEquals(KeyRange("5", "6"), result[5])
        assertEquals(KeyRange("6", "7"), result[6])
        assertEquals(KeyRange("7", "8"), result[7])
        assertEquals(KeyRange("8", "9"), result[8])
        assertEquals(KeyRange("9", "A"), result[9])
        assertEquals(KeyRange("A", "B"), result[10])
        assertEquals(KeyRange("B", "C"), result[11])
        assertEquals(KeyRange("C", "D"), result[12])
        assertEquals(KeyRange("D", "E"), result[13])
        assertEquals(KeyRange("E", "F"), result[14])
        assertEquals(KeyRange("F", "G"), result[15])
    }

    @Test
    fun `When start and end given with unknown characters, create valid ranges`() {
        val result = KeyRangeGenerator().generateRanges(16,"$", "}")

        assertEquals(16, result.size)
        assertEquals(KeyRange("$", "*"), result[0])
        assertEquals(KeyRange("*", "0"), result[1])
        assertEquals(KeyRange("0", "5"), result[2])
        assertEquals(KeyRange("5", "9"), result[3])
        assertEquals(KeyRange("9", "E"), result[4])
        assertEquals(KeyRange("E", "I"), result[5])
        assertEquals(KeyRange("I", "N"), result[6])
        assertEquals(KeyRange("N", "R"), result[7])
        assertEquals(KeyRange("R", "V"), result[8])
        assertEquals(KeyRange("V", "_"), result[9])
        assertEquals(KeyRange("_", "d"), result[10])
        assertEquals(KeyRange("d", "i"), result[11])
        assertEquals(KeyRange("i", "m"), result[12])
        assertEquals(KeyRange("m", "r"), result[13])
        assertEquals(KeyRange("r", "v"), result[14])
        assertEquals(KeyRange("v", "}"), result[15])
    }
}
