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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class KeySequenceGeneratorTest {

    @Test
    fun `When start is not given, create an empty sequence`() {
        assertFalse(KeySequenceGenerator().generateSequence(null, "42", 1).iterator().hasNext())
    }

    @Test
    fun `When start equal to end, create an empty sequence`() {
        assertFalse(KeySequenceGenerator().generateSequence("42", "42", 1).iterator().hasNext())
    }

    @Test
    fun `When start less than end, create a limited sequence`() {
        assertEquals(
            listOf("43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c", "4d", "4e", "4f", "50", "51", "52"),
            KeySequenceGenerator("0123456789abcdef").generateSequence("42", "52", 1).toList()
        )
    }

    @Test
    fun `When step is greater than one, create a limited sequence`() {
        assertEquals(
            listOf("44", "46", "48", "4a", "4c", "4e", "50", "52"),
            KeySequenceGenerator("0123456789abcdef").generateSequence("42", "52", 2).toList()
        )
    }

    @Test
    fun `When step less than zero, create a limited sequence`() {
        assertEquals(
            listOf("50", "4e", "4c", "4a", "48", "46", "44", "42"),
            KeySequenceGenerator("0123456789abcdef").generateSequence("52", "42", -2).toList()
        )
    }
}
