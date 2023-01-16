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

import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.types.Value
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.pow

class PowerWorker(
    private val value: WorkItemAccessor<Value>,
    worker: Worker
): ArithmeticWorker(value, worker) {

    override fun calc(left: Long, right: Long) =
        left.toDouble().pow(right.toDouble()).toLong()
    override fun calc(left: Double, right: Double) =
        left.pow(right)
    override fun calc(left: BigInteger, right: BigInteger): BigInteger =
        left.pow(right.toInt())
    override fun calc(left: BigDecimal, right: BigDecimal): BigDecimal =
        left.pow(right.toInt())
}
