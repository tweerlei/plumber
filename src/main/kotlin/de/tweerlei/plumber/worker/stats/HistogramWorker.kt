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
package de.tweerlei.plumber.worker.stats

import de.tweerlei.plumber.util.Histogram
import de.tweerlei.plumber.util.StringHistogram
import de.tweerlei.plumber.util.StringPacker
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import mu.KLogging

class HistogramWorker(
    private val name: String,
    size: Int,
    packer: StringPacker,
    prefix: String,
    worker: Worker
): DelegatingWorker(worker) {

    private val stringHistogram = StringHistogram(size, packer, prefix)
    private val numberHistogram = Histogram(size)

    companion object: KLogging()

    override fun doProcess(item: WorkItem) =
        when {
            item.getOptional<Any>() is Number -> numberHistogram.add(item.getLong())
            else -> stringHistogram.add(item.getString())
        }.let { true }

    override fun onClose() {
        mutableListOf(
            "Histogram for $name"
        ).apply {
            if (!numberHistogram.toRange().isEmpty()) {
                add("Total count: ${numberHistogram.count()}")
//                add("Reallocs: ${numberHistogram.reallocs()}")
                numberHistogram.toMap().forEach { (key, value) ->
                    add("$key : $value")
                }
            }
            if (!stringHistogram.toRange().isEmpty()) {
                add("Total count: ${stringHistogram.count()}")
//                add("Reallocs: ${stringHistogram.reallocs()}")
                stringHistogram.toMap().forEach { (key, value) ->
                    add("$key : $value")
                }
            }
        }.joinToString("\n")
        .also { logger.info(it) }
    }
}
