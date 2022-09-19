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

import java.util.TreeMap
import kotlin.math.min
import kotlin.math.round

class Histogram(
	private val maxBuckets: Int
) {
	
	private var buckets = IntArray(1)
	private var lowerBound: Long = Long.MAX_VALUE
	private var upperBound: Long = Long.MIN_VALUE
	private var reallocs: Int = 0
	
	fun add(value: Long) {
		if (value < lowerBound)
			resize(value, upperBound)
		if (value >= upperBound)
			resize(lowerBound, value + 1)
		
		buckets[bucketFor(value)]++
	}
	
	fun toRange() =
		lowerBound until upperBound
	
	fun toMap() =
		TreeMap<Long, Int>().also { map ->
			buckets.forEachIndexed { index, value ->
				map[valueFor(index)] = value
			}
		}
	
	fun count() =
		buckets.sum()
	
	fun reallocs() =
		reallocs
	
	private fun bucketFor(value: Long) =
		valueToIndex(value, lowerBound, upperBound, buckets.size)
	
	private fun valueFor(index: Int) =
		indexToValue(index, lowerBound, upperBound, buckets.size)
	
	private fun resize(l: Long, u: Long) {
		if (lowerBound > upperBound) {
			lowerBound = l
			upperBound = l + 1
			return
		}
		
		val newBuckets = IntArray(min(u - l, maxBuckets.toLong()).toInt())
		
		var oldIndex = -1
		var oldFrom = Long.MIN_VALUE
		var oldTo = lowerBound
		
		for (i in newBuckets.indices) {
			val newTo = indexToValue(i + 1, l, u, newBuckets.size)
			var newFrom = indexToValue(i, l, u, newBuckets.size)
			while (newFrom < newTo) {
				if (newFrom >= oldTo) {
					oldIndex++
					if (oldIndex >= buckets.size)
						break
					oldFrom = valueFor(oldIndex)
					oldTo = valueFor(oldIndex + 1)
				}
				if (newTo <= oldTo) {
					newBuckets[i] += buckets.getOrDefault(oldIndex, 0).scale(newTo - newFrom, oldTo - oldFrom)
					newFrom = newTo
				} else {
					newBuckets[i] += buckets.getOrDefault(oldIndex, 0).scale(oldTo - newFrom, oldTo - oldFrom)
					newFrom = oldTo
				}
			}
		}
		
		buckets = newBuckets
		lowerBound = l
		upperBound = u
		reallocs++
	}
	
	companion object {
		
		private fun valueToIndex(value: Long, lower: Long, upper: Long, size: Int) =
			((value - lower) * size / (upper - lower)).toInt()
		
		private fun indexToValue(index: Int, lower: Long, upper: Long, size: Int) =
			lower + (upper - lower) * index / size
		
		private fun Int.scale(num: Long, denom: Long) =
			round(toDouble() * num / denom).toInt()
		
		private fun IntArray.getOrDefault(index: Int, def: Int) =
			if (index < 0 || index >= size) def
			else get(index)
	}
}
