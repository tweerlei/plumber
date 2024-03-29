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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FormattingWorkerTest {

    @Test
    fun testSimple() {

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("foo")))
            .append { w -> FormattingWorker("Hello", w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe(StringValue.of("Hello"))
    }

    @Test
    fun testReplacement() {

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("foo"), "name" to StringValue.of("variable")))
            .append { w -> FormattingWorker("My @@{name} is {value}", w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe(StringValue.of("My @variable is {value}"))
    }

    @Test
    fun testNoReplacement() {

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("foo")))
            .append { w -> FormattingWorker("My @@{name} is {value}", w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe(StringValue.of("My @ is {value}"))
    }

    @Test
    fun testSpecialCharacters() {

        val item = TestWorkerRunner(WorkItem.of(StringValue.of("foo"), "name" to StringValue.of("foo@1bar")))
            .append { w -> FormattingWorker("My @@{name} is {value}", w) }
            .run()
            .singleOrNull()

        item.shouldNotBeNull()
        item.get().shouldBe(StringValue.of("My @foo@1bar is {value}"))
    }
}
