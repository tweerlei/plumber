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
package de.tweerlei.plumber.cmdline

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.DefaultApplicationArguments

data class CommandLine(
    val steps: List<Pair<String, String>>,
    val options: Map<String, String>
) {
    companion object {

        fun from(args: List<String>) =
            args
                .filterNot { line -> line.isBlank() || line.startsWith("#") }
                .let { lines -> DefaultApplicationArguments(*lines.toTypedArray()) }
                .let { parsedArgs -> from(parsedArgs) }

        fun from(args: ApplicationArguments) =
            CommandLine(
                args.parseSteps(),
                args.parseOptions()
            )

        private fun ApplicationArguments.parseSteps() =
            nonOptionArgs
                .map { step ->
                    step.split(":", ignoreCase = false, limit = 2)
                        .let { parts ->
                            when (parts.size) {
                                1 -> parts[0] to ""
                                else -> parts[0] to parts[1]
                            }
                        }
                }.toList()

        private fun ApplicationArguments.parseOptions() =
            optionNames.associateWith { option ->
                getOptionValues(option).firstOrNull() ?: "true"
            }
    }
}
