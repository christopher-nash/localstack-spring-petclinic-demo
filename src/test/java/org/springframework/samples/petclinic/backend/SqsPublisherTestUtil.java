package org.springframework.samples.petclinic.backend;

import java.util.List;
import java.util.Map;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import static java.lang.Integer.parseInt;
import static software.amazon.awssdk.services.sqs.model.QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES;

public class SqsPublisherTestUtil {

	private SqsPublisherTestUtil() {
		// Static Util Class, should not be instantiated
	}

	public static void purgeQueue(SqsClient sqsClient, String queueUrl) {
		sqsClient.purgeQueue(PurgeQueueRequest.builder()
								 .queueUrl(queueUrl)
								 .build());
	}

	public static int getNumberOfMessages(SqsClient sqsClient, String queueUrl) {
		final Map<String, String> attributes =
			sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
											 .queueUrl(queueUrl)
											 .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES)
											 .build()).attributesAsStrings();

		return parseInt(attributes.get(APPROXIMATE_NUMBER_OF_MESSAGES.toString()));
	}

	public static List<Message> getMessages(SqsClient sqsClient, String queueUrl) {
		return sqsClient.receiveMessage(ReceiveMessageRequest.builder()
											.queueUrl(queueUrl)
											.build())
			.messages();
	}

}
