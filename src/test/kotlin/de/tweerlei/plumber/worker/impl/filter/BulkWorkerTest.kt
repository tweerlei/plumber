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
package de.tweerlei.plumber.worker.impl.filter

import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.text.UUIDWorker
import de.tweerlei.plumber.worker.types.WorkItemList
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BulkWorkerTest {

    @Test
    fun `When bulking items Then all items are passed through`() {
        val items = TestWorkerRunner()
            .append { w -> UUIDWorker(19, w) }
            .append { w -> BulkWorker(10, w) }
            .run(WorkItem.from(""))

        items.size.shouldBe(2)
        items.fold(0) { acc, item ->
            acc + item.getAs<WorkItemList>(WellKnownKeys.WORK_ITEMS).size
        }.shouldBe(19)
    }
}
