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
package de.tweerlei.plumber.pipeline.steps.text

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.util.transform.TransformerFactory
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.text.DigestWorker
import org.springframework.stereotype.Service

@Service("digestWorker")
class DigestStep(
    private val factory: TransformerFactory
): ProcessingStep {

    override val group = "Text"
    override val name = "Calculate digest"
    override val description = "Calculate a message digest using the given algorithm"
    override val help = """
        Supported algorithms:
          gzip
          gunzip
          (any supported digest algorithm)
    """.trimIndent()
    override val options = ""
    override val example = """
        value::Hello
        digest:sha1
        text-write:hex
        lines-write  # result: f7ff9e8b7bb2e09b70935a5d785e0cc5d9d0abf0
    """.trimIndent()
    override val argDescription
        get() = algorithmFor("")
    override val argInterpolated = false

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.DIGEST,
        WellKnownKeys.DIGEST_ALGORITHM
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        DigestWorker(
            factory.getTransformer(arg),
            w
        )

    private fun algorithmFor(arg: String) =
        arg.ifEmpty { "sha1" }
}
