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

import de.tweerlei.plumber.worker.impl.TestWorkerRunner
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.types.StringValue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FindReplaceWorkerTest {

    @Test
    fun testFindMatch() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("foo.*bar"), w) }
            .run(WorkItem.from("foobazbar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("foobazbar")
    }

    @Test
    fun testFindMatchWithGroups() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("foo(.*)bar"), w) }
            .run(WorkItem.from("foobazbar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("baz")
        item.getAs<StringValue>("matchedGroup0").value.shouldBe("foobazbar")
        item.getAs<StringValue>("matchedGroup1").value.shouldBe("baz")
    }

    @Test
    fun testFindNoMatch() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("foo.*bar"), w) }
            .run(WorkItem.from("barfoo"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getOptional().shouldBeNull()
    }

    @Test
    fun testReplace() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("foo.*bar"), w) }
            .append { w -> ReplacingWorker({ StringValue("bar") }, w) }
            .run(WorkItem.from("foobazbar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("bar")
    }

    @Test
    fun testReplaceWithGroups() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("foo(.*)bar"), w) }
            .append { w -> ReplacingWorker({ StringValue("doo$1") }, w) }
            .run(WorkItem.from("0foobazbar1"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("0doobaz1")
    }

    @Test
    fun testReplaceWithAnchor() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("^foo(.*)bar$"), w) }
            .append { w -> ReplacingWorker({ StringValue("doo$1") }, w) }
            .run(WorkItem.from("foobazbar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("doobaz")
    }

    @Test
    fun testNoReplaceWithAnchor() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("^foo(.*)bar$"), w) }
            .append { w -> ReplacingWorker({ StringValue("doo$1") }, w) }
            .run(WorkItem.from("0foobazbar1"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("0foobazbar1")
    }
}
