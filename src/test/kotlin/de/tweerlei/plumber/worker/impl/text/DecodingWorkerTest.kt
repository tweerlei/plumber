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

import de.tweerlei.plumber.util.codec.Base64Codec
import de.tweerlei.plumber.util.codec.CharsetCodec
import de.tweerlei.plumber.util.codec.HexCodec
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.types.ByteArrayValue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class DecodingWorkerTest {

    @Test
    fun testBase64() {

        val value = byteArrayOf(42, -64, 0, 32, -128, 127)

        val item = TestWorkerRunner(WorkItem.from("KsAAIIB/"))
            .append { w -> DecodingWorker(Base64Codec(), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<ByteArrayValue>().value.contentEquals(value).shouldBeTrue()
    }

    @Test
    fun testHex() {

        val value = byteArrayOf(42, -64, 0, 32, -128, 127)

        val item = TestWorkerRunner(WorkItem.from("2ac00020807f"))
            .append { w -> DecodingWorker(HexCodec(), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<ByteArrayValue>().value.contentEquals(value).shouldBeTrue()
    }

    @Test
    fun testUTF8() {

        val value = byteArrayOf(98, 105, -61, -97)

        val item = TestWorkerRunner(WorkItem.from("bi\u00df"))
            .append { w -> DecodingWorker(CharsetCodec(StandardCharsets.UTF_8), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<ByteArrayValue>().value.contentEquals(value).shouldBeTrue()
    }
}
