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
package de.tweerlei.plumber.worker.s3

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.securitytoken.AWSSecurityTokenService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3ClientFactory(
    @Value("\${aws.region}") private val awsRegion: String,
    @Value("\${aws.s3.endpoint}") private val awsEndpoint: String,
    private val stsService: AWSSecurityTokenService
) {

    fun createAmazonS3Client(numberOfThreads: Int, roleArn: String?): AmazonS3 =
        AmazonS3ClientBuilder.standard().apply {
            if (awsEndpoint.isEmpty())
                withRegion(awsRegion)
            else
                withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion))

            if (roleArn != null) {
                withCredentials(
                    STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, "temp-session")
                        .withRoleSessionDurationSeconds(1800)
                        .withStsClient(stsService)
                        .build()
                )
            } else {
                withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            }

            withClientConfiguration(ClientConfiguration().withMaxConnections(numberOfThreads))
        }.build()
}
