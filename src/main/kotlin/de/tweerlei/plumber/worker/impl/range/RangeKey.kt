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
package de.tweerlei.plumber.worker.impl.range

import de.tweerlei.plumber.worker.types.ComparableValue
import de.tweerlei.plumber.worker.types.Range

enum class RangeKey {
    start {
        override fun get(range: Range): ComparableValue =
            range.startAfter

        override fun set(range: Range, value: ComparableValue) {
            range.startAfter = value
        }
    },
    end {
        override fun get(range: Range): ComparableValue =
            range.endWith

        override fun set(range: Range, value: ComparableValue) {
            range.endWith = value
        }
    };

    abstract fun get(range: Range): ComparableValue
    abstract fun set(range: Range, value: ComparableValue)
}
