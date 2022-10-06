package org.springframework.samples.petclinic.backend;

import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.config.LocalStackConfig;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.samples.petclinic.backend.SqsPublisherTestUtil.getMessages;
import static org.springframework.samples.petclinic.backend.SqsPublisherTestUtil.getNumberOfMessages;
import static org.springframework.samples.petclinic.backend.SqsPublisherTestUtil.purgeQueue;
import static org.springframework.samples.petclinic.config.LocalStackConfig.TEST_Q_NAME;

@SpringBootTest(classes = PetClinicApplication.class,
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	properties = "spring.autoconfigure.exclude=com.vaadin.flow.spring.SpringBootAutoConfiguration")
@Import(LocalStackConfig.class)
class SqsPublisherDockerMavenIT {

	@Autowired
	private SqsPublisher fixture;

	@Autowired
	private SqsClient sqsClient;

	private String qUrl;

	@BeforeEach
	public void setUp() {
		qUrl = sqsClient.getQueueUrl(builder -> builder.queueName(TEST_Q_NAME)).queueUrl();

		// Make sure that the queues are cleared. If tests fail they can "poison" other tests.
		purgeQueue(sqsClient, qUrl);
	}

	@Test
	void publish() {
		// Verify the queue is empty before starting the test
		assertThat(getNumberOfMessages(sqsClient, qUrl)).isZero();

		// Publish a test message
		fixture.publish("{\"some\":\"body\"}");

		// Verify that the message showed up in the queue
		assertThat(getNumberOfMessages(sqsClient, qUrl)).isEqualTo(1);

		// Get the message and verify its the message we enqueued
		Message message = getMessages(sqsClient, qUrl).get(0);
		assertThat(message.body()).isEqualTo("{\"some\":\"body\"}");
	}

	@Test
	void publishABunchOfMessages() {
		// Verify the queue is empty before starting the test
		assertThat(getNumberOfMessages(sqsClient, qUrl)).isZero();

		// Publish a bunch of messages
		IntStream.range(0, 10).forEach(value -> fixture.publish(String.format("{\"messageNum\":\"%d\"}", value)));

		// Verify that the message showed up in the queue
		assertThat(getNumberOfMessages(sqsClient, qUrl)).isEqualTo(10);
	}

}
