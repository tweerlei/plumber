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
import de.tweerlei.plumber.util.codec.Base64Codec
import de.tweerlei.plumber.util.codec.CodecFactory
import de.tweerlei.plumber.util.codec.HexCodec
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.text.EncodingWorker
import org.springframework.stereotype.Service

@Service("text-writeWorker")
class EncodeStep(
    private val factory: CodecFactory
): ProcessingStep {

    override val group = "Text"
    override val name = "Encode binary data"
    override val description = "Encode binary data as text using the given algorithm"
    override val help = """
        Supported algorithms:
          ${HexCodec.NAME}
          ${Base64Codec.NAME}
          (any supported character set)
    """.trimIndent()
    override val options = ""
    override val example = """
        value::Hello
        text-write:base64
        lines-write  # result: SGVsbG8=
    """.trimIndent()
    override val argDescription
        get() = encodingFor("")

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        EncodingWorker(
            factory.getCodec(arg),
            w
        )

    private fun encodingFor(arg: String) =
        arg.ifEmpty { "hex" }
}
