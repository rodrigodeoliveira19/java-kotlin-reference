package br.com.item.event;

import br.com.item.model.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class ItemSqsPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;
    private final String fifoQueueUrl;

    public ItemSqsPublisher(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            @Value("${aws.sqs.queue-url}") String queueUrl,
            @Value("${aws.sqs.fifo-queue-url}") String fifoQueueUrl
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
        this.fifoQueueUrl = fifoQueueUrl;
    }

    public void publish(Item event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(payload)
                    .build();

            sqsClient.sendMessage(request);
            System.out.println("Mensagem enviada ´para fila com sucesso: ID item"+event.getId());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao publicar evento", e);
        }
    }

    public void publishFifo(Item event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(fifoQueueUrl)
                    .messageBody(payload)
                    .messageGroupId("item-group")
                    .messageDeduplicationId(UUID.randomUUID().toString())
                    .build();

            sqsClient.sendMessage(request);
            System.out.println("Mensagem enviada ´para fila com sucesso: ID item"+event.getId());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao publicar evento", e);
        }
    }
}
