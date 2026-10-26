package BM_NotificationService.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionCompletedEvent {

    private Long transactionId;
    private Long accountNumber;

    // Owner of the account performing the transaction
    private Long userId;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private Long receiverAccountNumber;

    // Owner of the receiver account
    private Long receiverUserId;
    private LocalDateTime transactionTime;
    private BigDecimal balanceAfterTransaction;

    public TransactionCompletedEvent() {
    }


    // ==========================================
    // TRANSACTION ID
    // ==========================================

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }


    // ==========================================
    // ACCOUNT NUMBER
    // ==========================================

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }


    // ==========================================
    // USER ID
    // ==========================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    // ==========================================
    // AMOUNT
    // ==========================================

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    // ==========================================
    // TRANSACTION TYPE
    // ==========================================

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


    // ==========================================
    // STATUS
    // ==========================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // ==========================================
    // RECEIVER ACCOUNT
    // ==========================================

    public Long getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(Long receiverAccountNumber) {

        this.receiverAccountNumber = receiverAccountNumber;
    }


    // ==========================================
    // RECEIVER USER ID
    // ==========================================

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }


    // ==========================================
    // TRANSACTION TIME
    // ==========================================

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(
            LocalDateTime transactionTime) {

        this.transactionTime = transactionTime;
    }


    // ==========================================
    // BALANCE AFTER TRANSACTION
    // ==========================================

    public BigDecimal getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(BigDecimal balanceAfterTransaction) {

        this.balanceAfterTransaction = balanceAfterTransaction;
    }
}