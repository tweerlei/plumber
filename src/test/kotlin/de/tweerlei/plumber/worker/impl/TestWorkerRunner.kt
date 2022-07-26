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
package de.tweerlei.plumber.worker.impl

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.WorkerBuilder
import de.tweerlei.plumber.worker.impl.stats.CollectingWorker
import java.util.concurrent.ConcurrentLinkedQueue

class TestWorkerRunner {

    class DummyRunContext : Worker.RunContext {
        override fun isInterrupted() = false
        override fun isFailFast() = false
    }

    private var wb = WorkerBuilder.create()

    fun append(builder: (Worker) -> Worker) =
        this.apply {
            wb = wb.append(builder)
        }

    fun run(item: WorkItem): Collection<WorkItem> =
        ConcurrentLinkedQueue<WorkItem>().also { items ->
            wb.append { w -> CollectingWorker(items, w) }
                .build()
                .open(DummyRunContext())
                .use { w -> w.process(item) }
        }
}
