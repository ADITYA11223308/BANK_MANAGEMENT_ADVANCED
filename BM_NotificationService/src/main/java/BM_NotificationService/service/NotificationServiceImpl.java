package BM_NotificationService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import BM_NotificationService.dto.NotificationResponse;
import BM_NotificationService.entity.Notification;
import BM_NotificationService.entity.NotificationStatus;
import BM_NotificationService.event.TransactionCompletedEvent;
import BM_NotificationService.exception.NotificationNotFoundException;
import BM_NotificationService.repository.NotificationRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class NotificationServiceImpl implements NotificationService {
	
	private String buildMessage(TransactionCompletedEvent event) {

	    return event.getTransactionType() + " transaction of " + event.getAmount() + " completed successfully.";
	}

    private final NotificationRepository notificationRepository;
    private final HttpServletRequest httpServletRequest;

    public NotificationServiceImpl(NotificationRepository notificationRepository, HttpServletRequest httpServletRequest) {

        this.notificationRepository = notificationRepository;
        this.httpServletRequest = httpServletRequest;
    }

    // ==========================================
    // CREATE NOTIFICATION
    // ==========================================

    @Override
    @Transactional
    public void createNotification(
            TransactionCompletedEvent event) {
    	
    	if (event == null) {
            throw new IllegalArgumentException("Transaction event cannot be null");
        }

        if (event.getTransactionId() == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        
         //Kafka can deliver the same event more than once.  

        if (notificationRepository.existsByTransactionId(event.getTransactionId())) {
            return;
        }

        // SENDER NOTIFICATION

        Notification senderNotification = new Notification();

        senderNotification.setTransactionId(event.getTransactionId());
        senderNotification.setAccountNumber(event.getAccountNumber());
        senderNotification.setMessage(createSenderMessage(event));
        senderNotification.setStatus(NotificationStatus.UNREAD);
        senderNotification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(senderNotification);

        // RECEIVER NOTIFICATION

        if ("TRANSFER".equals(event.getTransactionType()) && event.getReceiverAccountNumber() != null) {

            Notification receiverNotification = new Notification();

            receiverNotification.setTransactionId(event.getTransactionId());
            receiverNotification.setAccountNumber(event.getReceiverAccountNumber());
            receiverNotification.setMessage("You received ₹" + event.getAmount() + " from account " + event.getAccountNumber() + ".");
            receiverNotification.setStatus(NotificationStatus.UNREAD);
            receiverNotification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(receiverNotification);
        }
    }

    // ==========================================
    // GET NOTIFICATIONS BY ACCOUNT
    // ==========================================

    @Override
    public List<NotificationResponse>
    getNotificationsByAccountNumber( Long accountNumber) {

        return notificationRepository
                .findByAccountNumberOrderByCreatedAtDesc(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET UNREAD NOTIFICATIONS
    // ==========================================

    @Override
    public List<NotificationResponse>
    getUnreadNotificationsByAccountNumber(Long accountNumber) {

        return notificationRepository
                .findByAccountNumberAndStatusOrderByCreatedAtDesc(accountNumber, NotificationStatus.UNREAD)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET NOTIFICATION BY ID
    // ==========================================

    @Override
    public NotificationResponse
    getNotificationById(Long notificationId) {

        Notification notification = notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + notificationId));

        return mapToResponse(notification);
    }

    // ==========================================
    // MARK AS READ
    // ==========================================

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + notificationId));

        notification.setStatus(NotificationStatus.READ);

        notificationRepository.save(notification);
    }

    // ==========================================
    // MARK ALL AS READ
    // ==========================================

    @Override
    @Transactional
    public void markAllAsRead(Long accountNumber) {

        List<Notification> notifications = notificationRepository
                        .findByAccountNumberAndStatusOrderByCreatedAtDesc(accountNumber, NotificationStatus.UNREAD);

        for (Notification notification : notifications) {

            notification.setStatus(NotificationStatus.READ);
        }

        notificationRepository.saveAll(notifications);
    }

    // ==========================================
    // SENDER MESSAGE
    // ==========================================

    private String createSenderMessage(TransactionCompletedEvent event) {

        switch (event.getTransactionType()) {

            case "DEPOSIT":
                return "Deposit of ₹" + event.getAmount() + " completed successfully.";

            case "WITHDRAW":
                return "Withdrawal of ₹" + event.getAmount() + " completed successfully.";

            case "TRANSFER":
                return "Transfer of ₹" + event.getAmount() + " to account " + event.getReceiverAccountNumber() + " completed successfully.";

            default:
                return "Transaction completed successfully.";
        }
    }

    // ==========================================
    // MAP ENTITY TO RESPONSE
    // ==========================================

    private NotificationResponse mapToResponse(Notification notification) {

        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getTransactionId(),
                notification.getAccountNumber(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getCreatedAt());
    }
}