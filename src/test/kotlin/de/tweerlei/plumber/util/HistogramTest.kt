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

class HistogramTest {
	
	@Test
	fun testPositive() {
		val h = Histogram(7)
		assertEquals(0, h.count())
		
		h.add(100)
		assertEquals(1, h.count())
		assertEquals(100L..100L, h.toRange())
		assertEquals(mapOf(
			100L to 1
		), h.toMap())
		
		h.add(799)
		assertEquals(2, h.count())
		assertEquals(100L..799L, h.toRange())
		assertEquals(mapOf(
			100L to 1,
			200L to 0,
			300L to 0,
			400L to 0,
			500L to 0,
			600L to 0,
			700L to 1
		), h.toMap())
		
		h.add(300)
		h.add(310)
		h.add(320)
		h.add(330)
		h.add(340)
		h.add(350)
		h.add(360)
		h.add(370)
		h.add(380)
		h.add(390)
		h.add(400)
		assertEquals(13, h.count())
		assertEquals(100L..799L, h.toRange())
		assertEquals(mapOf(
			100L to 1,
			200L to 0,
			300L to 10,
			400L to 1,
			500L to 0,
			600L to 0,
			700L to 1
		), h.toMap())
		
		h.add(65)
		h.add(834)
		assertEquals(15, h.count())
		assertEquals(65L..834L, h.toRange())
		assertEquals(mapOf(
			65L to 2,
			175L to 1,
			285L to 7,
			395L to 3,
			505L to 0,
			615L to 0,
			725L to 2
		), h.toMap())
	}

	@Test
	fun testNegative() {
		val h = Histogram(7)
		assertEquals(0, h.count())
		
		h.add(-800)
		assertEquals(1, h.count())
		assertEquals(-800L..-800L, h.toRange())
		assertEquals(mapOf(
			-800L to 1
		), h.toMap())
		
		h.add(-101)
		assertEquals(2, h.count())
		assertEquals(-800L..-101L, h.toRange())
		assertEquals(mapOf(
			-800L to 1,
			-700L to 0,
			-600L to 0,
			-500L to 0,
			-400L to 0,
			-300L to 0,
			-200L to 1
		), h.toMap())
		
		h.add(-600)
		h.add(-590)
		h.add(-580)
		h.add(-570)
		h.add(-560)
		h.add(-550)
		h.add(-540)
		h.add(-530)
		h.add(-520)
		h.add(-510)
		h.add(-500)
		assertEquals(13, h.count())
		assertEquals(-800L..-101L, h.toRange())
		assertEquals(mapOf(
			-800L to 1,
			-700L to 0,
			-600L to 10,
			-500L to 1,
			-400L to 0,
			-300L to 0,
			-200L to 1
		), h.toMap())
		
		h.add(-66)
		h.add(-835)
		assertEquals(15, h.count())
		assertEquals(-835L..-66L, h.toRange())
		assertEquals(mapOf(
			-835L to 2,
			-725L to 1,
			-615L to 7,
			-505L to 3,
			-395L to 0,
			-285L to 0,
			-175L to 2
		), h.toMap())
	}
}
