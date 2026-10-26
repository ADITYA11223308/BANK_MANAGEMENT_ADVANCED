package BM_TransactionService.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import BM_TransactionService.entity.TransactionStatus;
import BM_TransactionService.entity.TransactionType;

public class TransactionResponse {

    private Long transactionId;
    private Long accountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus status;
    private Long receiverAccountNumber;
    private LocalDateTime transactionTime;
    private BigDecimal balanceAfterTransaction;

    public TransactionResponse() {
    }

    public TransactionResponse(
            Long transactionId,
            Long accountNumber,
            BigDecimal amount,
            TransactionType transactionType,
            TransactionStatus status,
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
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