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

import java.io.File

class InclusionResolver {

    fun resolve(cmdline: CommandLine) =
        resolveAll(cmdline).let { inclusions ->
            if (inclusions.isEmpty()) cmdline
            else cmdline.merge(inclusions)
        }

    private fun resolveAll(cmdline: CommandLine): Map<Int, CommandLine> =
        mutableMapOf<Int, CommandLine>().apply {
            cmdline.steps.forEachIndexed { index, step ->
                if (step.first == "include") {
                    this[index] = loadFile(step.second) { parsedCmdline ->
                        resolve(parsedCmdline)
                    }
                }
            }
        }

    // TODO: Support other sources than local file system?
    private val includedFiles = mutableListOf<File>()

    private fun <T> loadFile(path: String, processor: (CommandLine) -> T) =
        File(path).let { fileToInclude ->
            if (includedFiles.isEmpty()) fileToInclude.absoluteFile
            else includedFiles.last().parentFile.resolve(fileToInclude)
        }.let { resolvedFile ->
            if (includedFiles.contains(resolvedFile))
                throw IllegalArgumentException("Detected recursive inclusion of $resolvedFile")
            includedFiles.add(resolvedFile)
            resolvedFile.readLines()
        }.let { lines ->
            CommandLine.from(lines)
        }.let { parsedCmdline ->
            processor(parsedCmdline)
        }.also {
            includedFiles.removeLast()
        }

    private fun CommandLine.merge(inclusions: Map<Int, CommandLine>) =
        mutableListOf<CommandLine>().apply {
            var lastIndex = 0
            inclusions.forEach { (index, cmdline) ->
                add(stepRange(lastIndex, index))
                add(cmdline)
                lastIndex = index + 1
            }
            add(stepRange(lastIndex, steps.size))
        }.let { parts ->
            val steps = mutableListOf<Pair<String, String>>()
            var options = emptyMap<String, String>()
            parts.forEach { cmdline ->
                steps.addAll(cmdline.steps)
                options = cmdline.options.plus(options)
            }
            CommandLine(steps, options)
        }

    private fun CommandLine.stepRange(from: Int, to: Int) =
        CommandLine(
            steps.subList(from, to),
            options
        )
}
