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
package de.tweerlei.plumber.worker.impl.node

import com.fasterxml.jackson.databind.JsonNode
import de.tweerlei.plumber.worker.types.*

fun JsonNode.toComparableValue(): Value =
    when {
        isBoolean -> BooleanValue.of(booleanValue())
        isLong -> LongValue.of(longValue())
        isInt -> LongValue.of(intValue())
        isShort -> LongValue.of(shortValue())
        isDouble -> DoubleValue.of(doubleValue())
        isFloat -> DoubleValue.of(floatValue())
        isBigInteger -> BigIntegerValue.of(bigIntegerValue())
        isBigDecimal -> BigDecimalValue.of(decimalValue())
        isTextual -> StringValue.of(textValue())
        isBinary -> ByteArrayValue.of(binaryValue())
        isNull -> NullValue.INSTANCE
        isEmpty -> NullValue.INSTANCE
        else -> Node(this)
    }
