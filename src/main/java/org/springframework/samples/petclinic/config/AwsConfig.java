/*
 * © 2019 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Spring configuration which enables interacting with AWS Services
 */
@Configuration
@Profile({ "production" })
public class AwsConfig {

  @Lazy
  @Bean(destroyMethod = "close")
  public SqsClient sqsClient() {
    return SqsClient.builder()
      .credentialsProvider(DefaultCredentialsProvider.create())
      .region(Region.US_EAST_1)
      .build();
  }

}
