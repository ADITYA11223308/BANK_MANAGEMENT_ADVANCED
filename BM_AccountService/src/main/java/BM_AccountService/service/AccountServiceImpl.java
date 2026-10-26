package BM_AccountService.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import BM_AccountService.dto.AccountRequest;
import BM_AccountService.dto.AccountResponse;
import BM_AccountService.dto.UserResponse;
import BM_AccountService.entity.Account;
import BM_AccountService.entity.AccountStatus;
import BM_AccountService.entity.BalanceOperation;
import BM_AccountService.exception.AccountAlreadyExistsException;
import BM_AccountService.exception.AccountNotFoundException;
import BM_AccountService.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            RestClient restClient,
            HttpServletRequest httpServletRequest) {

        this.accountRepository = accountRepository;
        this.restClient = restClient;
        this.httpServletRequest = httpServletRequest;
    }

    // CREATE ACCOUNT //
    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {

        // CUSTOMER can create an account only for themselves.
        // ADMIN can create an account for any user.

        if (!isAdmin() && !getLoggedInUserId().equals(accountRequest.getUserId())) {
            throw new AccessDeniedException("You can create an account only for yourself");
        }

        // Check whether user already has an account
        if (accountRepository.existsByUserId(accountRequest.getUserId())) {
            throw new AccountAlreadyExistsException("User already has an account");
        }

        // Get user details from User Service
        // Forward the current user's JWT.

        String authorization = httpServletRequest.getHeader("Authorization");

        UserResponse user = restClient.get()
                .uri("/users/{userId}", accountRequest.getUserId())
                .header("Authorization", authorization)
                .retrieve()
                .body(UserResponse.class);

        // Generate unique account number
        Long accountNumber = generateAccountNumber();

        // Create Account
        Account account = new Account();

        account.setAccountNumber(accountNumber);
        account.setUserId(user.getUserId());
        account.setAccountHolderName(user.getName());
        account.setAccountType(accountRequest.getAccountType());
        account.setBalance(accountRequest.getInitialBalance());
        account.setStatus(AccountStatus.ACTIVE);

        // Save Account
        Account savedAccount = accountRepository.save(account);

        // Return response
        return mapToResponse(savedAccount);
    }

    // GET ACCOUNT BY ACCOUNT NUMBER //
    @Override
    @Cacheable(value = "accounts", key = "#accountNumber")
    public AccountResponse getAccountByAccountNumber(Long accountNumber) {
        Account account = accountRepository
                        .findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        // CUSTOMER can access only their own account.
        // ADMIN can access any account.
        checkAccountOwnership(account);
        return mapToResponse(account);
    }

    // GET ACCOUNT BY USER ID //
    @Override
    @Cacheable(value = "accountsByUser", key = "#userId")
    public AccountResponse getAccountByUserId(Long userId) {
        // CUSTOMER can request only their own userId.
        // ADMIN can request any userId.
        if (!isAdmin() && !getLoggedInUserId().equals(userId)) {
            throw new AccessDeniedException(
                    "You are not authorized to access this account");
        }

        Account account = accountRepository
                        .findByUserId(userId)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + userId));
        return mapToResponse(account);
    }

    // GET ALL ACCOUNTS //
    @Override
    public List<AccountResponse> getAllAccounts() {

        // Only ADMIN can see all accounts.
        if (!isAdmin()) {
            throw new AccessDeniedException("Only ADMIN can access all accounts");
        }

        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // UPDATE ACCOUNT STATUS //
    @Override
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public AccountResponse updateAccountStatus(Long accountNumber, AccountStatus status) {
        // Only ADMIN can change account status.
        if (!isAdmin()) {
            throw new AccessDeniedException("Only ADMIN can update account status");
        }

        Account account = accountRepository
                        .findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        account.setStatus(status);
        Account updatedAccount = accountRepository.save(account);

        return mapToResponse(updatedAccount);
    }

    // DELETE ACCOUNT //
    @Override
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public void deleteAccount(Long accountNumber) {

        // Only ADMIN can delete accounts.
        if (!isAdmin()) {
            throw new AccessDeniedException("Only ADMIN can delete an account");
        }

        Account account = accountRepository
                        .findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        accountRepository.delete(account);
    }

    // UPDATE BALANCE //
    @Override
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public AccountResponse updateBalance( Long accountNumber, BigDecimal amount, BalanceOperation operation) {

        Account account = accountRepository
                        .findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        // CUSTOMER can deposit/withdraw only from their own account.
        // ADMIN can operate on any account.
        checkAccountOwnership(account);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (operation == BalanceOperation.DEPOSIT) {

            account.setBalance(account.getBalance().add(amount));
        } 
        else if (operation == BalanceOperation.WITHDRAW) {

            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance");
            }

            account.setBalance(account.getBalance().subtract(amount));
        }

        Account updatedAccount = accountRepository.save(account);

        return mapToResponse(updatedAccount);
    }

    // GET ACCOUNT BY ID //
    @Override
    public AccountResponse getAccountById(Long accountId) {

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        // CUSTOMER can access only their own account.
        // ADMIN can access any account.

        checkAccountOwnership(account);

        return mapToResponse(account);
    }

    // CHECK WHETHER CURRENT USER IS ADMIN //
    private boolean isAdmin() {
    	
    		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    		return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    // GET LOGGED-IN USER ID FROM JWT //
    private Long getLoggedInUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }

    // CHECK ACCOUNT OWNERSHIP //
    private void checkAccountOwnership(
            Account account) {

        // ADMIN can access any account.

        if (isAdmin()) {
            return;
        }

        // CUSTOMER can access only their own account.

        Long loggedInUserId = getLoggedInUserId();
        
     /*  System.out.println("========== AUTHORIZATION DEBUG ==========");
        System.out.println("JWT user ID      : " + loggedInUserId);
        System.out.println("Account user ID  : " + account.getUserId());
        System.out.println("Account number   : " + account.getAccountNumber());
        System.out.println("Role             : " + authentication.getAuthorities());
        System.out.println("========================================="); */

        if (!account.getUserId().equals(loggedInUserId)) {
            throw new AccessDeniedException(
                    "You are not authorized to access this account");
        }
    } 
    
    // GENERATE ACCOUNT NUMBER//
    private Long generateAccountNumber() {

        Long accountNumber;

        do {

            accountNumber = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    // MAP ENTITY TO RESPONSE //
   private AccountResponse mapToResponse(Account account) {

        return new AccountResponse(
                account.getAccountId(),
                account.getAccountNumber(),
                account.getUserId(),
                account.getAccountHolderName(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus()
        );
    }

   @Override
   @CacheEvict(value = "accounts", allEntries = true)
   public AccountResponse depositForTransfer(
           Long senderAccountNumber,
           Long receiverAccountNumber,
           BigDecimal amount) {

       // Validate sender ownership
       Account sender = accountRepository
               .findByAccountNumber(senderAccountNumber)
               .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

       checkAccountOwnership(sender);

       // Validate amount
       if (amount == null || amount.signum() <= 0) {
           throw new IllegalArgumentException("Amount must be greater than zero");
       }

       // Find receiver without applying sender ownership
       Account receiver = accountRepository
               .findByAccountNumber(receiverAccountNumber)
               .orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));

       if (receiver.getStatus() != AccountStatus.ACTIVE) {
           throw new IllegalStateException("Receiver account is not active");
       }

       // Deposit into receiver account
       receiver.setBalance(receiver.getBalance().add(amount));

       Account updatedReceiver = accountRepository.save(receiver);

       return mapToResponse(updatedReceiver);
   }
   
}