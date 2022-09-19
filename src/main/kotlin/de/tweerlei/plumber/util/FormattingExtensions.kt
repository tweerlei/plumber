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
package de.tweerlei.plumber.util

import java.time.Duration
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

private val MAGNITUDES = mapOf(
    1000.0 * 1000.0 * 1000.0 to "%.2fG",
    1000.0 * 1000.0 to "%.2fM",
    1000.0 to "%.2fk"
)

fun Number.humanReadable() =
    toDouble().let { value ->
        MAGNITUDES.firstNotNullOfOrNull { (scale, fmt) ->
            if (value >= scale) fmt.format(value / scale) else null
        } ?: "%.2f".format(value)
    }

fun Duration.humanReadable() =
    String.format("%d:%02d:%02d.%03d", toHours(), toMinutesPart(), toSecondsPart(), toMillisPart())

fun Throwable.printStackTraceUpTo(caller: KClass<*>) =
    StringBuilder().also { sb ->
        sb.append(javaClass.name)
        sb.append(": ")
        sb.append(message)
        stackTrace.all { frame ->
            sb.append("\n\tat ")
            sb.append(frame.className)
            sb.append(".")
            sb.append(frame.methodName)
            sb.append("(")
            sb.append(frame.fileName)
            sb.append(":")
            sb.append(frame.lineNumber)
            sb.append(")")
            frame.className != caller.jvmName
        }
    }.toString()
