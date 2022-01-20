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
package de.tweerlei.plumber.worker

import de.tweerlei.plumber.worker.filter.UUIDWorker
import de.tweerlei.plumber.worker.stats.CollectingWorker
import de.tweerlei.plumber.worker.stats.CountingWorker
import de.tweerlei.plumber.worker.filter.MultithreadedWorker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentLinkedQueue

class MultithreadedWorkerTest {

    @Test
    fun `When using multiple threads Then all items are passed through`() {
        val items = ConcurrentLinkedQueue<WorkItem>()
        WorkerBuilder.create()
            .append { w -> UUIDWorker(100, w) }
            .append { w -> MultithreadedWorker("parallel", 4, 4, w) }
            .append { w -> CountingWorker("test", 100, w) }
            .append { w -> CollectingWorker(items, w) }
            .build()
            .open()
            .use { worker -> worker.process(WorkItem.of("")) }

        assertEquals(100, items.size)
    }
}
