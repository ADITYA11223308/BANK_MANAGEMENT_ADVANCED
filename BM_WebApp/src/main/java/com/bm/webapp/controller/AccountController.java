package com.bm.webapp.controller;

import com.bm.webapp.dto.AccountRequest;
import com.bm.webapp.dto.AccountResponse;
import com.bm.webapp.dto.enums.AccountType;
import com.bm.webapp.util.GatewayErrorUtil;
import com.bm.webapp.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final RestClient gatewayClient;

    public AccountController(RestClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @GetMapping
    public String viewAccount(HttpSession session, Model model) {

        model.addAttribute("user", SessionHelper.currentUser(session));

        try {
            AccountResponse account = gatewayClient.get()
                    .uri("/accounts/user/{userId}", SessionHelper.userId(session))
                    .header("Authorization", SessionHelper.authHeader(session))
                    .retrieve()
                    .body(AccountResponse.class);

            model.addAttribute("account", account);
            return "account";

        } catch (RestClientResponseException ex) {
            // No account yet - send them to create one.
            return "redirect:/account/create";
        }
    }

    @GetMapping("/create")
    public String createAccountPage(HttpSession session, Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        model.addAttribute("accountTypes", AccountType.values());
        return "create-account";
    }

    @PostMapping("/create")
    public String createAccount(@RequestParam AccountType accountType,
                                 @RequestParam BigDecimal initialBalance,
                                 HttpSession session,
                                 Model model) {

        model.addAttribute("user", SessionHelper.currentUser(session));

        AccountRequest request = new AccountRequest();
        request.setUserId(SessionHelper.userId(session));
        request.setAccountType(accountType);
        request.setInitialBalance(initialBalance);

        try {
            gatewayClient.post()
                    .uri("/accounts")
                    .header("Authorization", SessionHelper.authHeader(session))
                    .body(request)
                    .retrieve()
                    .body(AccountResponse.class);

            return "redirect:/dashboard";

        } catch (RestClientResponseException ex) {
            model.addAttribute("error", GatewayErrorUtil.extractMessage(ex));
            model.addAttribute("accountTypes", AccountType.values());
            return "create-account";
        }
    }
}
