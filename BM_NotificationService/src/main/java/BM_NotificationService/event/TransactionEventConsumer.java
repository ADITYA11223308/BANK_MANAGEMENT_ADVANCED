package BM_NotificationService.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import BM_NotificationService.service.NotificationService;

@Service
public class TransactionEventConsumer {

    private final NotificationService notificationService;

    public TransactionEventConsumer(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "transaction-completed",
            groupId = "notification-service"
    )
    public void consume(TransactionCompletedEvent event) {

        System.out.println("========== KAFKA EVENT RECEIVED ==========");
        System.out.println("Transaction ID: " + event.getTransactionId());
        System.out.println("Account Number: " + event.getAccountNumber());
        System.out.println("Amount: " + event.getAmount());
        System.out.println( "Transaction Type: " + event.getTransactionType());
        System.out.println("Status: " + event.getStatus());

        /*
         * If this method throws an exception,
         * Spring Kafka's CommonErrorHandler handles it.
         *
         * Retry -> Retry -> Retry -> DLQ
         */
        notificationService.createNotification(event);

        System.out.println("========== NOTIFICATION CREATED ==========");
    }
}