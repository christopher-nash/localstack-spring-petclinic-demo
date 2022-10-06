/*
 * © 2021 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.springframework.samples.petclinic.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

/**
 * Spring configuration which enables interacting with various AWS services via <a
 * href="https://github.com/localstack/localstack">LocalStack</a>
 */
@Configuration
@Profile({ "!production" })
public class LocalStackConfig {

	public static final String TEST_Q_NAME = "petclinic-q";

	@Profile({ "!testcontainers" })
	@Lazy
	@Bean(destroyMethod = "close")
	public SqsClient sqsClient(@Value("#{systemProperties['localstack.port'] ?: '4566'}") String port)
		throws URISyntaxException {

		SqsClient sqs = SqsClient.builder()
			.endpointOverride(new URI("http://localhost:" + port))
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("accesskey", "secretkey")))
			.region(Region.US_EAST_1)
			.build();

		createTestQ(sqs);

		return sqs;
	}

	@Profile({ "testcontainers" })
	@Lazy
	@Bean(destroyMethod = "close")
	public SqsClient sqsClientTestContainers(@Value("${localstack.endpoint}") String endpoint,
											 @Value("${localstack.credentials.accessKey}") String accessKey,
											 @Value("${localstack.credentials.secretKey}") String secretKey)
		throws URISyntaxException {

		SqsClient sqs = SqsClient.builder()
			.endpointOverride(new URI(endpoint))
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
			.region(Region.US_EAST_1)
			.build();

		createTestQ(sqs);

		return sqs;
	}

	private void createTestQ(SqsClient sqs) {
		// Set the visibility timeout to 1. This more accurately mimics how things are with the _real_ SQS where things
		// aren't visible right away. Without some visibility delay, the SQS client call to get messages can end up fetching
		// the same message multiple times causing test fragility.
		EnumMap<QueueAttributeName, String> qAttributes = new EnumMap<>(QueueAttributeName.class);
		qAttributes.put(QueueAttributeName.VISIBILITY_TIMEOUT, "1");

		// Create the Q in the localstack container
		sqs.createQueue(builder -> builder.queueName(TEST_Q_NAME).attributes(qAttributes));
	}

}
