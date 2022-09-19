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
package de.tweerlei.plumber.worker.impl.stats

import de.tweerlei.plumber.util.range.Histogram
import de.tweerlei.plumber.util.range.StringHistogram
import de.tweerlei.plumber.util.range.StringPacker
import de.tweerlei.plumber.util.extractCommonPrefix
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.types.ComparableValue
import de.tweerlei.plumber.worker.types.NullValue
import de.tweerlei.plumber.worker.types.NumberValue
import mu.KLogging

class HistogramWorker(
    private val name: String,
    size: Int,
    keyChars: String,
    startAfterKey: ComparableValue,
    stopAfterKey: ComparableValue,
    worker: Worker
): DelegatingWorker(worker) {

    private val stringHistogram = when {
            startAfterKey is NullValue -> ""
            stopAfterKey is NullValue -> ""
            else -> extractCommonPrefix(startAfterKey.toString(), stopAfterKey.toString())
        }.let { prefix ->
            StringHistogram(size, StringPacker(keyChars), prefix)
        }
    private val numberHistogram = Histogram(size)

    companion object: KLogging()

    override fun doProcess(item: WorkItem) =
        item.get().let { value ->
            when (value) {
                is NumberValue -> numberHistogram.add(value.toNumber().toLong())
                else -> stringHistogram.add(value.toString())
            }
        }.let { true }

    override fun onClose() {
        logger.info {
            mutableListOf(
                "Histogram for $name"
            ).apply {
                if (!numberHistogram.toRange().isEmpty()) {
                    add("Total count: ${numberHistogram.count()}")
//                    add("Reallocs: ${numberHistogram.reallocs()}")
                    numberHistogram.toMap().forEach { (key, value) ->
                        add("$key : $value")
                    }
                }
                if (!stringHistogram.toRange().isEmpty()) {
                    add("Total count: ${stringHistogram.count()}")
//                    add("Reallocs: ${stringHistogram.reallocs()}")
                    stringHistogram.toMap().forEach { (key, value) ->
                        add("$key : $value")
                    }
                }
            }.joinToString("\n")
        }
    }
}
