package com.bm.webapp.dto;

import com.bm.webapp.dto.enums.AccountStatus;
import com.bm.webapp.dto.enums.AccountType;
import java.math.BigDecimal;

public class AccountResponse {

    private Long accountId;
    private Long accountNumber;
    private Long userId;
    private String accountHolderName;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;

    public AccountResponse() {}

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Long getAccountNumber() { return accountNumber; }
    public void setAccountNumber(Long accountNumber) { this.accountNumber = accountNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
}
