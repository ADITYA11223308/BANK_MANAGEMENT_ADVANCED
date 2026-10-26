package com.bm.webapp.controller;

import com.bm.webapp.dto.AccountResponse;
import com.bm.webapp.dto.TransactionResponse;
import com.bm.webapp.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Controller
public class DashboardController {

    private final RestClient gatewayClient;

    public DashboardController(RestClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        model.addAttribute("user", SessionHelper.currentUser(session));

        AccountResponse account = fetchAccountOrNull(session);
        model.addAttribute("account", account);

        if (account != null) {
            List<TransactionResponse> transactions = gatewayClient.get()
                    .uri("/transactions/account/{accountNumber}", account.getAccountNumber())
                    .header("Authorization", SessionHelper.authHeader(session))
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<List<TransactionResponse>>() {});

            if (transactions == null) {
                transactions = List.of();
            }

            List<TransactionResponse> recent = transactions.size() > 5
                    ? transactions.subList(0, 5)
                    : transactions;

            model.addAttribute("recentTransactions", recent);
        }

        return "dashboard";
    }

    private AccountResponse fetchAccountOrNull(HttpSession session) {
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
}
