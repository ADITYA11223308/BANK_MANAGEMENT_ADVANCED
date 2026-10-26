package com.bm.webapp.controller;

import com.bm.webapp.dto.UserRequest;
import com.bm.webapp.dto.UserResponse;
import com.bm.webapp.util.GatewayErrorUtil;
import com.bm.webapp.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final RestClient gatewayClient;

    public ProfileController(RestClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        return "profile";
    }

    @GetMapping("/edit")
    public String editPage(HttpSession session, Model model) {
        model.addAttribute("user", SessionHelper.currentUser(session));
        return "profile-edit";
    }

    // NOTE: PUT /users/{userId} on the backend replaces the whole record,
    // so the current password must always be re-submitted here even when
    // only the name or phone number is changing.
    @PostMapping("/edit")
    public String edit(@RequestParam String name,
                        @RequestParam String phoneNumber,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        UserResponse existing = SessionHelper.currentUser(session);

        UserRequest request = new UserRequest();
        request.setName(name);
        request.setEmail(existing.getEmail());
        request.setPhoneNumber(phoneNumber);
        request.setPassword(password);
        request.setRole(existing.getRole());

        try {
            UserResponse updated = gatewayClient.put()
                    .uri("/users/{userId}", SessionHelper.userId(session))
                    .header("Authorization", SessionHelper.authHeader(session))
                    .body(request)
                    .retrieve()
                    .body(UserResponse.class);

            session.setAttribute("currentUser", updated);
            return "redirect:/profile";

        } catch (RestClientResponseException ex) {
            model.addAttribute("error", GatewayErrorUtil.extractMessage(ex));
            model.addAttribute("user", existing);
            return "profile-edit";
        }
    }
}
