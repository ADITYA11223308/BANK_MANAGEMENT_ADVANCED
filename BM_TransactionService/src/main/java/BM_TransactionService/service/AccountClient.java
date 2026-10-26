package BM_TransactionService.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import BM_TransactionService.dto.AccountResponse;
import BM_TransactionService.entity.BalanceOperation;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AccountClient {

    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;

    public AccountClient(
            RestClient restClient,
            HttpServletRequest httpServletRequest) {

        this.restClient = restClient;
        this.httpServletRequest = httpServletRequest;
    }

    @Retry(name = "accountService")
    public AccountResponse getAccount(Long accountNumber) {

        String authorization =
                httpServletRequest.getHeader("Authorization");

        return restClient.get()
                .uri(
                        "/accounts/{accountNumber}",
                        accountNumber)
                .header(
                        "Authorization",
                        authorization)
                .retrieve()
                .body(AccountResponse.class);
    }

    @Retry(name = "accountService")
    public AccountResponse updateBalance(
            Long accountNumber,
            BigDecimal amount,
            BalanceOperation operation,
            String idempotencyKey) {

        String authorization =
                httpServletRequest.getHeader("Authorization");

        return restClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(
                                "/accounts/{accountNumber}/balance")
                        .queryParam(
                                "amount",
                                amount)
                        .queryParam(
                                "operation",
                                operation)
                        .build(accountNumber))
                .header(
                        "Authorization",
                        authorization)
                .header(
                        "Idempotency-Key",
                        idempotencyKey)
                .retrieve()
                .body(AccountResponse.class);
    }
}