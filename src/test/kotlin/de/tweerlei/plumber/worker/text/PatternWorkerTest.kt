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
package de.tweerlei.plumber.worker.text

import de.tweerlei.plumber.worker.TestWorkerRunner
import de.tweerlei.plumber.worker.WorkItem
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PatternWorkerTest {

    @Test
    fun testFindNoMatch() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("He..o"), w) }
            .run(WorkItem.of("foo"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getOptional().shouldBeNull()
    }

    @Test
    fun testFindMatch() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("He..o"), w) }
            .run(WorkItem.of("foo Hello bar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe("Hello")
    }

    @Test
    fun testReplace() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("(\\S+)-(\\S+)"), w) }
            .append { w -> ReplacingWorker("$2_$1", w) }
            .run(WorkItem.of("foo x-1 yz-23 bar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe("foo 1_x 23_yz bar")
        item.get("matchedGroup0").shouldBe("x-1")
        item.get("matchedGroup1").shouldBe("x")
        item.get("matchedGroup2").shouldBe("1")
    }

    @Test
    fun testReplaceWithoutMatch() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("(\\S+)-(\\S+)"), w) }
            .append { w -> ReplacingWorker("$2_$1", w) }
            .run(WorkItem.of("foo x_1 yz_23 bar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe("foo x_1 yz_23 bar")
    }
}
