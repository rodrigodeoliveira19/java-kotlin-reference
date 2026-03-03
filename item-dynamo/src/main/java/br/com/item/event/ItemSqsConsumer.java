package br.com.item.event;

import br.com.item.model.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class ItemSqsConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public ItemSqsConsumer(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            @Value("${aws.sqs.queue-url}") String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    @Scheduled(fixedDelay = 5000)
    public void pollMessages() {

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20) // Long polling - “Se não houver mensagem agora, espere até 20 segundos antes de
                // responder - Menos custo”
                .maxNumberOfMessages(10)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();

        System.out.println("Encontrado item: "+ messages.size());
        for (Message message : messages) {
            processMessage(message);
        }
    }

    private void processMessage(Message message) {

        try {
            System.out.println("Iniciando o processando do item");

            Item event =
                    objectMapper.readValue(message.body(), Item.class);

            // 🔥 PROCESSAMENTO IDPOTENTE AQUI
            handleBusinessLogic(event);

            deleteMessage(message);

        } catch (Exception e) {
            // NÃO delete a mensagem
            // SQS vai tentar novamente
            System.err.println("Erro ao processar mensagem: " + e.getMessage());
        }
    }

    private void deleteMessage(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }

    private void handleBusinessLogic(Item event) {
        System.out.println("Processando item: " + event.getDescription());
    }
}
