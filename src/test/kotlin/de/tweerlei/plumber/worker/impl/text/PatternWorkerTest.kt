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

class PatternWorkerTest {

    @Test
    fun testFindNoMatch() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("He..o"), w) }
            .run(WorkItem.from("foo"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getOptional().shouldBeNull()
    }

    @Test
    fun testFindMatch() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("He..o"), w) }
            .run(WorkItem.from("foo Hello bar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("Hello")
    }

    @Test
    fun testReplace() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("(\\S+)-(\\S+)"), w) }
            .append { w -> ReplacingWorker({ StringValue("$2_$1") }, w) }
            .run(WorkItem.from("foo x-1 yz-23 bar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("foo 1_x 23_yz bar")
        item.getAs<StringValue>("matchedGroup0").value.shouldBe("x-1")
        item.getAs<StringValue>("matchedGroup1").value.shouldBe("x")
        item.getAs<StringValue>("matchedGroup2").value.shouldBe("1")
    }

    @Test
    fun testReplaceWithoutMatch() {

        val item = TestWorkerRunner()
            .append { w -> MatchingWorker(Regex("(\\S+)-(\\S+)"), w) }
            .append { w -> ReplacingWorker({ StringValue("$2_$1") }, w) }
            .run(WorkItem.from("foo x_1 yz_23 bar"))
            .singleOrNull()

        item.shouldNotBeNull()
        item.getAs<StringValue>().value.shouldBe("foo x_1 yz_23 bar")
    }
}
