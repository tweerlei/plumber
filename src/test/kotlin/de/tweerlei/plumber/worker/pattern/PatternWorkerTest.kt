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
package de.tweerlei.plumber.worker.pattern

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkerBuilder
import de.tweerlei.plumber.worker.stats.CollectingWorker
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PatternWorkerTest {

    @Test
    fun testFindNoMatch() {

        val items = mutableListOf<WorkItem>()
        WorkerBuilder.create()
            .append { w -> MatchingWorker(Regex("He..o"), w) }
            .append { w -> CollectingWorker(items, w) }
            .build()
            .process(WorkItem.of("foo"))

        items.size.shouldBe(1)
        items[0].getOptional().shouldBe(null)
    }

    @Test
    fun testFindMatch() {

        val items = mutableListOf<WorkItem>()
        WorkerBuilder.create()
            .append { w -> MatchingWorker(Regex("He..o"), w) }
            .append { w -> CollectingWorker(items, w) }
            .build()
            .process(WorkItem.of("foo Hello bar"))

        items.size.shouldBe(1)
        items[0].get().shouldBe("Hello")
    }

    @Test
    fun testReplace() {

        val items = mutableListOf<WorkItem>()
        WorkerBuilder.create()
            .append { w -> MatchingWorker(Regex("(\\S+)-(\\S+)"), w) }
            .append { w -> ReplacingWorker("$2_$1", w) }
            .append { w -> CollectingWorker(items, w) }
            .build()
            .process(WorkItem.of("foo x-1 yz-23 bar"))

        items.size.shouldBe(1)
        items[0].get().shouldBe("foo 1_x 23_yz bar")
        items[0].get("matchedGroup0").shouldBe("x-1")
        items[0].get("matchedGroup1").shouldBe("x")
        items[0].get("matchedGroup2").shouldBe("1")
    }

    @Test
    fun testReplaceWithoutMatch() {

        val items = mutableListOf<WorkItem>()
        WorkerBuilder.create()
            .append { w -> MatchingWorker(Regex("(\\S+)-(\\S+)"), w) }
            .append { w -> ReplacingWorker("$2_$1", w) }
            .append { w -> CollectingWorker(items, w) }
            .build()
            .process(WorkItem.of("foo x_1 yz_23 bar"))

        items.size.shouldBe(1)
        items[0].get().shouldBe("foo x_1 yz_23 bar")
    }
}
