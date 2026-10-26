package com.bm.webapp.controller;

import com.bm.webapp.dto.LoginRequest;
import com.bm.webapp.dto.LoginResponse;
import com.bm.webapp.dto.UserRequest;
import com.bm.webapp.dto.UserResponse;
import com.bm.webapp.dto.enums.UserRole;
import com.bm.webapp.util.GatewayErrorUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Controller
public class AuthController {

    private final RestClient gatewayClient;

    public AuthController(RestClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    // ---------------- LOGIN ----------------

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String registered, Model model) {
        if (registered != null) {
            model.addAttribute("success", "Account created successfully. Please log in.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {

        try {
            LoginResponse loginResponse = gatewayClient.post()
                    .uri("/auth/login")
                    .body(new LoginRequest(email, password))
                    .retrieve()
                    .body(LoginResponse.class);

            establishSession(session, loginResponse);

            return "redirect:/dashboard";

        } catch (RestClientResponseException ex) {
            model.addAttribute("error", GatewayErrorUtil.extractMessage(ex));
            model.addAttribute("email", email);
            return "login";
        }
    }

    // ---------------- REGISTER ----------------

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String phoneNumber,
                            @RequestParam String password,
                            @RequestParam String confirmPassword,
                            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("phoneNumber", phoneNumber);
            return "register";
        }

        UserRequest request = new UserRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setPhoneNumber(phoneNumber);
        request.setRole(UserRole.CUSTOMER);

        try {
            gatewayClient.post()
                    .uri("/users/register")
                    .body(request)
                    .retrieve()
                    .body(UserResponse.class);

            return "redirect:/login?registered";

        } catch (RestClientResponseException ex) {
            model.addAttribute("error", GatewayErrorUtil.extractMessage(ex));
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("phoneNumber", phoneNumber);
            return "register";
        }
    }

    // ---------------- LOGOUT ----------------

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ---------------- HELPERS ----------------

    private void establishSession(HttpSession session, LoginResponse loginResponse) {

        session.setAttribute("jwt", loginResponse.getToken());
        session.setAttribute("userId", loginResponse.getUserId());

        UserResponse profile = gatewayClient.get()
                .uri("/users/{userId}", loginResponse.getUserId())
                .header("Authorization", "Bearer " + loginResponse.getToken())
                .retrieve()
                .body(UserResponse.class);

        session.setAttribute("currentUser", profile);
    }
}
