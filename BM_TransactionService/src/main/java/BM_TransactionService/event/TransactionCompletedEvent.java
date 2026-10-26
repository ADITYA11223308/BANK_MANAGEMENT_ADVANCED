package BM_TransactionService.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionCompletedEvent {

    private Long transactionId;
    private Long accountNumber;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private Long receiverAccountNumber;
    private LocalDateTime transactionTime;
    private BigDecimal balanceAfterTransaction;

    public TransactionCompletedEvent() {
    }

    public TransactionCompletedEvent(
            Long transactionId,
            Long accountNumber,
            BigDecimal amount,
            String transactionType,
            String status,
            Long receiverAccountNumber,
            LocalDateTime transactionTime,
            BigDecimal balanceAfterTransaction) {

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status;
        this.receiverAccountNumber = receiverAccountNumber;
        this.transactionTime = transactionTime;
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(Long receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public BigDecimal getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(
            BigDecimal balanceAfterTransaction) {

        this.balanceAfterTransaction = balanceAfterTransaction;
    }
}