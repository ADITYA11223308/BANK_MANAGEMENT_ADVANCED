package BM_NotificationService.service;

import java.util.List;

import BM_NotificationService.dto.NotificationResponse;
import BM_NotificationService.event.TransactionCompletedEvent;

public interface NotificationService {

    void createNotification(TransactionCompletedEvent event);

    List<NotificationResponse> getNotificationsByAccountNumber(Long accountNumber);
    List<NotificationResponse> getUnreadNotificationsByAccountNumber(Long accountNumber);

    NotificationResponse getNotificationById(Long notificationId);

    void markAsRead(Long notificationId);
    void markAllAsRead(Long accountNumber);
}