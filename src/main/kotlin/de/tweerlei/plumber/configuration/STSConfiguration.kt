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
package de.tweerlei.plumber.configuration

import com.amazonaws.auth.*
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class STSConfiguration(
    @Value("\${aws.region}") val awsRegion: String,
    @Value("\${aws.sts.endpoint}") val awsEndpoint: String,
) {

    @Bean
    fun createAmazonSTSClient() =
        AWSSecurityTokenServiceAsyncClientBuilder.standard().apply {
            if (awsEndpoint.isEmpty())
                withRegion(awsRegion)
            else
                withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion))

            withCredentials(AWSCredentialsProviderChain(
                EnvironmentVariableCredentialsProvider(),
                EC2ContainerCredentialsProviderWrapper()
            ))
        }.build()
}
