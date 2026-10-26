package BM_NotificationService.dto;

import java.time.LocalDateTime;

import BM_NotificationService.entity.NotificationStatus;

public class NotificationResponse {

    private Long notificationId;
    private Long transactionId;
    private Long accountNumber;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;

    public NotificationResponse(
            Long notificationId,
            Long transactionId,
            Long accountNumber,
            String message,
            NotificationStatus status,
            LocalDateTime createdAt) {

        this.notificationId = notificationId;
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public String getMessage() {
        return message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}