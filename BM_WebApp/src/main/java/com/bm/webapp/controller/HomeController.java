package com.bm.webapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landing(HttpSession session) {
        // If already logged in, skip the marketing page and go straight to the dashboard.
        if (session.getAttribute("jwt") != null) {
            return "redirect:/dashboard";
        }
        return "landing";
    }
}
