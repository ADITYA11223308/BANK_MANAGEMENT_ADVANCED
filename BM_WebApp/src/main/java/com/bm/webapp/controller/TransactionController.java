package com.bm.webapp.controller;

import com.bm.webapp.dto.AccountResponse;
import com.bm.webapp.dto.TransactionRequest;
import com.bm.webapp.dto.TransactionResponse;
import com.bm.webapp.util.GatewayErrorUtil;
import com.bm.webapp.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * NOTE on Idempotency-Key: the backend's /transactions/deposit, /withdraw and
 * /transfer endpoints now require this header (@RequestHeader("Idempotency-Key"),
 * no default), so every POST here must send one or the gateway call fails with
 * 400 Bad Request before it even reaches the service layer.
 *
 * A fresh key is generated into the model on every render of a transaction
 * page (GET, and again after a POST - success or failure) and carried as a
 * hidden form field. That way:
 *  - a genuine double-submit of the SAME rendered page (double click, a
 *    resubmitted form) reuses the same key, so the backend's idempotency
 *    check dedupes it and the second attempt returns the original result
 *    instead of double-processing the money movement.
 *  - a deliberate second operation (user submits again after seeing the
 *    result) gets a brand new key, since the page was re-rendered with one,
 *    so it's correctly treated as a new transaction.
 */
@Controller
public class TransactionController {

    private final RestClient gatewayClient;

    public TransactionController(RestClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    // ---------------- DEPOSIT ----------------

    @GetMapping("/deposit")
    public String depositPage(HttpSession session, Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        model.addAttribute("account", currentAccount(session));
        model.addAttribute("idempotencyKey", newIdempotencyKey());
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam BigDecimal amount,
                           @RequestParam String idempotencyKey,
                           HttpSession session,
                           Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        AccountResponse account = currentAccount(session);
        model.addAttribute("account", account);

        if (account == null) {
            return "redirect:/account/create";
        }

        try {
            TransactionRequest request = new TransactionRequest();
            request.setAccountNumber(account.getAccountNumber());
            request.setAmount(amount);

            gatewayClient.post()
                    .uri("/transactions/deposit")
                    .header("Authorization", SessionHelper.authHeader(session))
                    .header("Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(TransactionResponse.class);

            model.addAttribute("success", "Deposit of ₹" + amount + " completed successfully.");
            model.addAttribute("account", currentAccount(session));

        } catch (RestClientResponseException ex) {
            model.addAttribute("error", GatewayErrorUtil.extractMessage(ex));
        }

        model.addAttribute("idempotencyKey", newIdempotencyKey());
        return "deposit";
    }

    // ---------------- WITHDRAW ----------------

    @GetMapping("/withdraw")
    public String withdrawPage(HttpSession session, Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        model.addAttribute("account", currentAccount(session));
        model.addAttribute("idempotencyKey", newIdempotencyKey());
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam BigDecimal amount,
                            @RequestParam String idempotencyKey,
                            HttpSession session,
                            Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        AccountResponse account = currentAccount(session);
        model.addAttribute("account", account);

        if (account == null) {
            return "redirect:/account/create";
        }

        try {
            TransactionRequest request = new TransactionRequest();
            request.setAccountNumber(account.getAccountNumber());
            request.setAmount(amount);

            gatewayClient.post()
                    .uri("/transactions/withdraw")
                    .header("Authorization", SessionHelper.authHeader(session))
                    .header("Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(TransactionResponse.class);

            model.addAttribute("success", "Withdrawal of ₹" + amount + " completed successfully.");
            model.addAttribute("account", currentAccount(session));

        } catch (RestClientResponseException ex) {
            model.addAttribute("error", GatewayErrorUtil.extractMessage(ex));
        }

        model.addAttribute("idempotencyKey", newIdempotencyKey());
        return "withdraw";
    }

    // ---------------- TRANSFER ----------------

    @GetMapping("/transfer")
    public String transferPage(HttpSession session, Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        model.addAttribute("account", currentAccount(session));
        model.addAttribute("idempotencyKey", newIdempotencyKey());
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam BigDecimal amount,
                            @RequestParam Long receiverAccountNumber,
                            @RequestParam String idempotencyKey,
                            HttpSession session,
                            Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        AccountResponse account = currentAccount(session);
        model.addAttribute("account", account);

        if (account == null) {
            return "redirect:/account/create";
        }

        try {
            TransactionRequest request = new TransactionRequest();
            request.setAccountNumber(account.getAccountNumber());
            request.setAmount(amount);
            request.setReceiverAccountNumber(receiverAccountNumber);

            gatewayClient.post()
                    .uri("/transactions/transfer")
                    .header("Authorization", SessionHelper.authHeader(session))
                    .header("Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(TransactionResponse.class);

            model.addAttribute("success", "Transfer of ₹" + amount + " to account "
                    + receiverAccountNumber + " completed successfully.");
            model.addAttribute("account", currentAccount(session));

        } catch (RestClientResponseException ex) {
            model.addAttribute("error", GatewayErrorUtil.extractMessage(ex));
        }

        model.addAttribute("idempotencyKey", newIdempotencyKey());
        return "transfer";
    }

    // ---------------- HISTORY ----------------

    @GetMapping("/transactions")
    public String history(HttpSession session, Model model) {

        model.addAttribute("user", SessionHelper.currentUser(session));
        AccountResponse account = currentAccount(session);
        model.addAttribute("account", account);

        if (account == null) {
            model.addAttribute("transactions", List.of());
            return "transactions";
        }

        List<TransactionResponse> transactions = gatewayClient.get()
                .uri("/transactions/account/{accountNumber}", account.getAccountNumber())
                .header("Authorization", SessionHelper.authHeader(session))
                .retrieve()
                .body(new ParameterizedTypeReference<List<TransactionResponse>>() {});

        model.addAttribute("transactions", transactions != null ? transactions : List.of());
        return "transactions";
    }

    // ---------------- HELPERS ----------------

    private AccountResponse currentAccount(HttpSession session) {
        try {
            return gatewayClient.get()
                    .uri("/accounts/user/{userId}", SessionHelper.userId(session))
                    .header("Authorization", SessionHelper.authHeader(session))
                    .retrieve()
                    .body(AccountResponse.class);
        } catch (RestClientResponseException ex) {
            return null;
        }
    }

    private String newIdempotencyKey() {
        return UUID.randomUUID().toString();
    }
}
