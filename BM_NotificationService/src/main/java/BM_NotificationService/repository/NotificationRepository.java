package BM_NotificationService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import BM_NotificationService.entity.Notification;
import BM_NotificationService.entity.NotificationStatus;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAccountNumberOrderByCreatedAtDesc(Long accountNumber);

    List<Notification> findByAccountNumberAndStatusOrderByCreatedAtDesc(Long accountNumber, NotificationStatus status);

    boolean existsByTransactionId(Long transactionId);
}