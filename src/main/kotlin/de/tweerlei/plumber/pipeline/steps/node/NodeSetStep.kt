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
package de.tweerlei.plumber.pipeline.steps.node

import de.tweerlei.plumber.pipeline.PipelineParams
import de.tweerlei.plumber.pipeline.steps.ProcessingStep
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.impl.node.NodeModifyWorker
import org.springframework.stereotype.Service

@Service("node-setWorker")
class NodeSetStep: ProcessingStep {

    override val group = "Nodes"
    override val name = "Set JSON path"
    override val description = "Replace a subtree of a JSON object using the given JSONPath"
    override val help = """
        The current node will be modified.
    """.trimIndent()
    override val options = ""
    override val example = """
        value:'{"numbers":[1,2,3]}'
        json-parse
        value:4
        node-set:numbers/1
        json-write  # result: {"numbers":[1,4,3]}
    """.trimIndent()
    override val argDescription = "<path>"
    override val argInterpolated = false

    override fun producedAttributesFor(arg: String) = setOf(
        WellKnownKeys.NODE
    )

    override fun createWorker(
        arg: String,
        w: Worker,
        predecessorName: String,
        params: PipelineParams,
        parallelDegree: Int
    ) =
        NodeModifyWorker(arg.toJsonPointer(), w)
}
