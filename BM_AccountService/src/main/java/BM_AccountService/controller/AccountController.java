package BM_AccountService.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import BM_AccountService.dto.AccountRequest;
import BM_AccountService.dto.AccountResponse;
import BM_AccountService.entity.AccountStatus;
import BM_AccountService.entity.BalanceOperation;
import BM_AccountService.service.AccountService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest accountRequest) {

        AccountResponse response = accountService.createAccount(accountRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/{receiverAccountNumber}/transfer-deposit")
    public ResponseEntity<AccountResponse> depositForTransfer(
            @PathVariable Long receiverAccountNumber,
            @RequestParam Long senderAccountNumber,
            @RequestParam BigDecimal amount) {

        AccountResponse response = accountService.depositForTransfer(senderAccountNumber, receiverAccountNumber, amount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber:\\d{8}}")
    public ResponseEntity<AccountResponse> getAccountByAccountNumber(@PathVariable Long accountNumber) {

        AccountResponse response = accountService.getAccountByAccountNumber(accountNumber);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{accountId:\\d{1,7}}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long accountId) {

        AccountResponse response = accountService.getAccountById(accountId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountResponse> getAccountByUserId(@PathVariable Long userId) {

        AccountResponse response = accountService.getAccountByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {

        List<AccountResponse> accounts = accountService.getAllAccounts();

        return ResponseEntity.ok(accounts);
    }

    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(@PathVariable Long accountNumber, @RequestParam AccountStatus status){

        AccountResponse response = accountService.updateAccountStatus(accountNumber, status);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountNumber}/balance")
    public ResponseEntity<AccountResponse> updateBalance(
            @PathVariable Long accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam BalanceOperation operation) {

        AccountResponse response = accountService.updateBalance(accountNumber, amount, operation);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountNumber) {

        accountService.deleteAccount(accountNumber);

        return ResponseEntity.ok("Account deleted successfully");
    }
}