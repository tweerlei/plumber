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
package de.tweerlei.plumber.worker.types

interface NumberValue: ComparableValue {

    override fun toByteArray() =
        toNumber().toLong().let { value ->
            // TODO: Big endian only
            byteArrayOf(
                (value and 0xff).toByte(),
                (value shr 8 and 0xff).toByte(),
                (value shr 16 and 0xff).toByte(),
                (value shr 24 and 0xff).toByte(),
                (value shr 32 and 0xff).toByte(),
                (value shr 40 and 0xff).toByte(),
                (value shr 48 and 0xff).toByte(),
                (value shr 56 and 0xff).toByte(),
            )
        }
    override fun size() =
        toString().length.toLong()
}
