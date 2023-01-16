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
package de.tweerlei.plumber.worker.impl.math

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import de.tweerlei.plumber.worker.types.*
import java.math.BigDecimal
import java.math.BigInteger

abstract class ArithmeticWorker(
    private val value: WorkItemAccessor<Value>,
    worker: Worker
): DelegatingWorker(worker) {

    override final fun doProcess(item: WorkItem) =
        item.get()
            .let { left ->
                value(item).let { right ->
                    when {
                        left is NullValue || right is NullValue ->
                            NullValue.INSTANCE
                        left is BigDecimalValue || right is BigDecimalValue ->
                            calc(left.toBigDecimal(), right.toBigDecimal())
                        left is BigIntegerValue || right is BigIntegerValue ->
                            calc(left.toBigInteger(), right.toBigInteger())
                        left is DoubleValue || right is DoubleValue ->
                            calc(left.toDouble(), right.toDouble())
                        left is InstantValue && right is InstantValue ->
                            calc(left.toLong(), right.toLong())
                        left is InstantValue || right is InstantValue ->
                            calc(left.toLong(), right.toLong())
                        left is DurationValue || right is DurationValue ->
                            calc(left.toLong(), right.toLong())
                        else ->
                            calc(left.toLong(), right.toLong())
                    }
                }
            }.also {
                item.set(it)
            }.let { true }

    protected abstract fun calc(left: Long, right: Long): ComparableValue
    protected abstract fun calc(left: Double, right: Double): ComparableValue
    protected abstract fun calc(left: BigInteger, right: BigInteger): ComparableValue
    protected abstract fun calc(left: BigDecimal, right: BigDecimal): ComparableValue
}
