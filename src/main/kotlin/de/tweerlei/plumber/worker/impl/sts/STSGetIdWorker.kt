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
package de.tweerlei.plumber.worker.impl.sts

import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceAsync
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest
import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.Worker
import de.tweerlei.plumber.worker.impl.DelegatingWorker
import mu.KLogging

class STSGetIdWorker(
    private val amazonSTSClient: AWSSecurityTokenServiceAsync,
    worker: Worker
): DelegatingWorker(worker) {

    companion object : KLogging()

    override fun doProcess(item: WorkItem) =
        getCallerIdentity()
            .let { response ->
                logger.debug { "AWS principal ARN: ${response.arn}" }
                logger.debug { "AWS user ID: ${response.userId}" }
                item.set(response.account)
            }.let { true }

    private fun getCallerIdentity() =
        GetCallerIdentityRequest()
            .let { request -> amazonSTSClient.getCallerIdentity(request) }
}
