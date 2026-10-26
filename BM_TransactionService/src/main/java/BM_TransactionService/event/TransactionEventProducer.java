package BM_TransactionService.event;

import java.util.concurrent.ExecutionException;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventProducer {

    private static final String TOPIC =
            "transaction-completed";

    private final KafkaTemplate<
            String,
            TransactionCompletedEvent> kafkaTemplate;

    public TransactionEventProducer(
            KafkaTemplate<
                    String,
                    TransactionCompletedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransactionCompleted(
            TransactionCompletedEvent event) {

        try {

            kafkaTemplate.send(
                    TOPIC,
                    String.valueOf(
                            event.getTransactionId()),
                    event
            ).get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Kafka publishing was interrupted",
                    e);

        } catch (ExecutionException e) {

            throw new RuntimeException(
                    "Failed to publish transaction event",
                    e);
        }
    }
}