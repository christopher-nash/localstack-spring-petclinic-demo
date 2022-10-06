package org.springframework.samples.petclinic.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@Slf4j
public class SqsPublisher {

	private final SqsClient sqs;

	public SqsPublisher(SqsClient sqs) {
		this.sqs = sqs;
	}

	public void publish(String messageBody) {
		log.info("Sending SQS Message. message='{}'", messageBody);
		sqs.sendMessage(SendMessageRequest.builder()
							.queueUrl("petclinic-q")
							.messageBody(messageBody)
							.build());
	}

}
