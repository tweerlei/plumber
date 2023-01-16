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
                            BigDecimalValue.of(calc(left.toBigDecimal(), right.toBigDecimal()))
                        left is BigIntegerValue || right is BigIntegerValue ->
                            BigIntegerValue.of(calc(left.toBigInteger(), right.toBigInteger()))
                        left is DoubleValue || right is DoubleValue ->
                            DoubleValue.of(calc(left.toDouble(), right.toDouble()).safeTruncate())
                        left is InstantValue && right is InstantValue ->
                            DurationValue.ofMillis(calc(left.toLong(), right.toLong()))
                        left is InstantValue || right is InstantValue ->
                            InstantValue.ofEpochMilli(calc(left.toLong(), right.toLong()))
                        left is DurationValue || right is DurationValue ->
                            DurationValue.ofMillis(calc(left.toLong(), right.toLong()))
                        else ->
                            LongValue.of(calc(left.toLong(), right.toLong()))
                    }
                }
            }.toValue()
            .also {
                item.set(it)
            }.let { true }

    protected abstract fun calc(left: Long, right: Long): Long
    protected abstract fun calc(left: Double, right: Double): Double
    protected abstract fun calc(left: BigInteger, right: BigInteger): BigInteger
    protected abstract fun calc(left: BigDecimal, right: BigDecimal): BigDecimal
}
