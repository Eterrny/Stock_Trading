package ru.ssu.springproject.stocks_trading.handlers;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger logger = Logger.getLogger(CustomLogoutSuccessHandler.class.getName());

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (authentication != null) {
            logger.info("User " + authentication.getName() + " has logged out.");
        }
        response.sendRedirect("/login?logout");
    }
}
