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

import de.tweerlei.plumber.util.range.StringHistogram
import de.tweerlei.plumber.util.range.StringPacker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringHistogramTest {

    @Test
    fun testAdd() {
        val histogram = StringHistogram(7, StringPacker("0123456789"), "007")

        histogram.add("0070")
        histogram.add("0076")

        histogram.add("0071")
        histogram.add("0072")
        histogram.add("0073")
        histogram.add("0074")
        histogram.add("0075")

        assertEquals(7, histogram.count())
        assertEquals(mapOf(
            "0070" to 1,
            "007083683683683683" to 1,
            "007168368368368368" to 1,
            "007252052052052052" to 1,
            "007336836836836837" to 1,
            "007420520520520521" to 1,
            "007505205205205206" to 1
        ), histogram.toMap())
    }

    @Test
    fun testAddShort() {
        val histogram = StringHistogram(7, StringPacker("0123456789", 2), "007")

        histogram.add("0070")
        histogram.add("0076")

        histogram.add("0071")
        histogram.add("0072")
        histogram.add("0073")
        histogram.add("0074")
        histogram.add("0075")

        assertEquals(7, histogram.count())
        assertEquals(mapOf(
            "0070" to 1,
            "00708" to 1,
            "00717" to 1,
            "00725" to 1,
            "00734" to 1,
            "00742" to 1,
            "00751" to 1
        ), histogram.toMap())
    }

    @Test
    fun testAddUnmapped() {
        val histogram = StringHistogram(7, StringPacker("0123456789"), "007")

        histogram.add("007")
        histogram.add("007Hello, World!")

        assertEquals(2, histogram.count())
        assertEquals(mapOf(
            "007" to 1,
            "007052050536836683" to 0,
            "007205202083683368" to 0,
            "007368363720520052" to 0,
            "007520515268367837" to 0,
            "007683676905204521" to 0,
            "007836828452051206" to 1
        ), histogram.toMap())
    }
}
