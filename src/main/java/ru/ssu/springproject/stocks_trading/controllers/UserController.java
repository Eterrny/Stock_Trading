package ru.ssu.springproject.stocks_trading.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.services.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/current-user")
    @ResponseBody
    public User getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername());
    }

    @GetMapping("/current-user-tariff")
    @ResponseBody
    public String getCurrentUserTariff(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername()).getTariff();
    }
}


