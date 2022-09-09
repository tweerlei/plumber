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
package de.tweerlei.plumber.worker.impl.text

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.types.coerceToString
import java.nio.charset.Charset
import java.util.*

class DecodingWorker(
    private val alg: String,
    worker: Worker
): DelegatingWorker(worker) {

    override fun doProcess(item: WorkItem) =
        item.getOptional().coerceToString()
            .let { encoded ->
                when (alg) {
                    "base64" -> decodeBase64(encoded)
                    "hex" -> decodeHex(encoded)
                    else -> encoded.toByteArray(Charset.forName(alg))
                }.also { bytes ->
                    item.set(bytes)
                }
            }.let { true }

    private fun decodeBase64(encoded: String): ByteArray =
        Base64.getDecoder().decode(encoded)

    private fun decodeHex(encoded: String) =
        encoded.split(Regex(".."))
            .let { bytes ->
                ByteArray(bytes.size)
                .also { arr ->
                    bytes.forEachIndexed { index, s -> arr[index] = s.toByte(16) }
                }
            }
}