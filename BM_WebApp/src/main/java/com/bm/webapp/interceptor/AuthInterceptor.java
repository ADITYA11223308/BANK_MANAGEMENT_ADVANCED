package com.bm.webapp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guards every page that requires a logged-in user.
 * A user is "logged in" once the JWT returned by POST /auth/login
 * has been stored in the HTTP session (see AuthController).
 */
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_JWT = "jwt";
    public static final String SESSION_USER = "currentUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);

        boolean loggedIn = session != null && session.getAttribute(SESSION_JWT) != null;

        if (!loggedIn) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
