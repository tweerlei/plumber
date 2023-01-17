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

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.types.StringValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FilteringWorkerTest {

    @Test
    fun `When processing items Then only matching ones are passed through`() {
        val allItems = listOf(
            WorkItem.of(StringValue.of("one")),
            WorkItem.of(StringValue.of("two")),
            WorkItem.of(StringValue.of("three")),
            WorkItem.of(StringValue.of("four")),
        )

        val items = TestWorkerRunner()
            .append { w -> CollectionWorker(allItems, allItems.size.toLong(), w) }
            .append { w -> FilteringWorker({ item -> item.get().toString().startsWith("t") }, w) }
            .run().toList()

        items.size.shouldBe(2)
        items[0].get().shouldBe(StringValue.of("two"))
        items[1].get().shouldBe(StringValue.of("three"))
    }
}
