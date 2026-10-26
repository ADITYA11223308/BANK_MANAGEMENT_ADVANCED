package com.bm.webapp.util;

import com.bm.webapp.dto.UserResponse;
import jakarta.servlet.http.HttpSession;

public final class SessionHelper {

    private SessionHelper() {}

    public static String jwt(HttpSession session) {
        return (String) session.getAttribute("jwt");
    }

    public static String authHeader(HttpSession session) {
        return "Bearer " + jwt(session);
    }

    public static Long userId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    public static UserResponse currentUser(HttpSession session) {
        return (UserResponse) session.getAttribute("currentUser");
    }
}
