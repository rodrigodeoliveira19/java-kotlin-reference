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
    private final String fifoQueueUrl;

    public ItemSqsConsumer(
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

    @Scheduled(fixedDelay = 5000)
    public void pollMessages() {

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20) // Long polling - “Se não houver mensagem agora, espere até 20 segundos antes de
                // responder - Menos custo”
                .maxNumberOfMessages(10)
                .build();

        pollAndProcessMessages(request);
    }

    @Scheduled(fixedDelay = 10000)
    public void pollFifoMessages() {

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(fifoQueueUrl)
                .waitTimeSeconds(20) // Long polling - “Se não houver mensagem agora, espere até 20 segundos antes de
                // responder - Menos custo”
                .maxNumberOfMessages(10)
                .build();

        pollAndProcessMessages(request);
    }

    private void pollAndProcessMessages(ReceiveMessageRequest request) {
        List<Message> messages = sqsClient.receiveMessage(request).messages();

        String queue = request.queueUrl();
        for (Message message : messages) {
            System.out.println("Processando item da fila: "+ queue +" Item: "+message.body());
            processMessage(message, queue);
        }
    }

    private void processMessage(Message message, String queue) {

        try {
            Item event =
                    objectMapper.readValue(message.body(), Item.class);

            // 🔥 PROCESSAMENTO IDPOTENTE AQUI
            handleBusinessLogic(event);

            deleteMessage(message, queue);

        } catch (Exception e) {
            // NÃO delete a mensagem
            // SQS vai tentar novamente
            System.err.println("Erro ao processar mensagem: " + e.getMessage());
        }
    }

    private void deleteMessage(Message message,String queueUrl) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }

    private void handleBusinessLogic(Item event) {
        System.out.println("Processando item: " + event.toString());
    }
}
