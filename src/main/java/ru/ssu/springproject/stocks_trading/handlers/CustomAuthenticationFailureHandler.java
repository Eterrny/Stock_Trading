package ru.ssu.springproject.stocks_trading.handlers;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger logger = Logger.getLogger(CustomAuthenticationFailureHandler.class.getName());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.warning("Failed login attempt: " + exception.getMessage());
        response.sendRedirect("/login?error");
    }
}