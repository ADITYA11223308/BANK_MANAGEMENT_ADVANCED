package BM_AccountService.dto;

import java.math.BigDecimal;

import BM_AccountService.entity.AccountStatus;
import BM_AccountService.entity.AccountType;

public class AccountResponse {

    private Long accountId;
    private Long accountNumber;
    private Long userId;
    private String accountHolderName;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;

    public AccountResponse() {
    }

    public AccountResponse(Long accountId,
                           Long accountNumber,
                           Long userId,
                           String accountHolderName,
                           AccountType accountType,
                           BigDecimal balance,
                           AccountStatus status) {

        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}