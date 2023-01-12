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

import de.tweerlei.plumber.util.transform.DigestTransformer
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.types.ByteArrayValue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test

class DigestWorkerTest {

    @Test
    fun testSha1() {

        val value = byteArrayOf(42, -64, 0, 32, -128, 127)
        val digest = byteArrayOf(-82, 112, -122, 92, -82, -36, -128, -27, -47, 53, -96, -8, 16, -122, 100, -29, 122, -44, -51, -64)

        val item = TestWorkerRunner(WorkItem.of(ByteArrayValue.of(value)))
            .append { w -> DigestWorker(DigestTransformer("sha1"), w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().toByteArray().contentEquals(digest).shouldBeTrue()
    }
}
