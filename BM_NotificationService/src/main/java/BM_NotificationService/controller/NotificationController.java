package BM_NotificationService.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import BM_NotificationService.dto.NotificationResponse;
import BM_NotificationService.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // =====================================================
    // GET ALL NOTIFICATIONS
    // =====================================================

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<NotificationResponse>>
    getNotificationsByAccountNumber(@PathVariable Long accountNumber) {

        return ResponseEntity.ok(
                notificationService
                        .getNotificationsByAccountNumber(
                                accountNumber));
    }

    // =====================================================
    // GET UNREAD NOTIFICATIONS
    // =====================================================

    @GetMapping("/account/{accountNumber}/unread")
    public ResponseEntity<List<NotificationResponse>>
    getUnreadNotificationsByAccountNumber(
            @PathVariable Long accountNumber) {

        return ResponseEntity.ok(
                notificationService
                        .getUnreadNotificationsByAccountNumber(
                                accountNumber));
    }

    // =====================================================
    // GET ONE NOTIFICATION
    // =====================================================

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse>
    getNotificationById(
            @PathVariable Long notificationId) {

        return ResponseEntity.ok(
                notificationService
                        .getNotificationById(
                                notificationId));
    }

    // =====================================================
    // MARK ONE AS READ
    // =====================================================

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String>
    markAsRead(
            @PathVariable Long notificationId) {

        notificationService
                .markAsRead(notificationId);

        return ResponseEntity.ok(
                "Notification marked as read");
    }

    // =====================================================
    // MARK ALL AS READ
    // =====================================================

    @PutMapping("/account/{accountNumber}/read-all")
    public ResponseEntity<String>
    markAllAsRead(
            @PathVariable Long accountNumber) {

        notificationService
                .markAllAsRead(accountNumber);

        return ResponseEntity.ok(
                "All notifications marked as read");
    }
}