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
package de.tweerlei.plumber.util.codec

class HexCodec: Codec {

    companion object {
        const val NAME = "hex"
    }

    override val name = NAME
    
    override fun toByteArray(value: String) =
        ByteArray(value.length / 2).also { bytes ->
            value.chunkedSequence(2)
                .forEachIndexed { index, byte ->
                    bytes[index] = byte.toShort(16).toByte()
                }
        }

    override fun toString(value: ByteArray): String =
        value.joinToString("") { cb ->
            String.format("%02x", cb)
        }
}
