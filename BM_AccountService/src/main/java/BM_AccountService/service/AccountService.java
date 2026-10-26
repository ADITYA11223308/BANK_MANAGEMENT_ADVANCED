package BM_AccountService.service;

import java.math.BigDecimal;
import java.util.List;

import BM_AccountService.dto.AccountRequest;
import BM_AccountService.dto.AccountResponse;
import BM_AccountService.entity.AccountStatus;
import BM_AccountService.entity.BalanceOperation;

public interface AccountService {

    AccountResponse createAccount(AccountRequest accountRequest);

    AccountResponse getAccountByAccountNumber(Long accountNumber);
    
    AccountResponse getAccountById(Long accountId);

    AccountResponse getAccountByUserId(Long userId);
    
    AccountResponse updateBalance(
            Long accountNumber,
            BigDecimal amount,
            BalanceOperation operation);

    List<AccountResponse> getAllAccounts();

    AccountResponse updateAccountStatus(
            Long accountNumber,
            AccountStatus status);
    
    AccountResponse depositForTransfer(
            Long senderAccountNumber,
            Long receiverAccountNumber,
            BigDecimal amount);

    void deleteAccount(Long accountNumber);
    
}