package ru.ssu.springproject.stocks_trading.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.repositories.TariffRepository;
import ru.ssu.springproject.stocks_trading.services.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final TariffRepository tariffRepository;

    @GetMapping("/current-user")
    @ResponseBody
    public User getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername());
    }

    @GetMapping("/current-user-tariff")
    @ResponseBody
    public String getCurrentUserCommissionRate(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        String tariffName = user.getTariff();
        log.info("Fetching commission rate for user: {} with tariff: {}", username, tariffName);
        Double commissionRate = tariffRepository.findCommissionPercentageByName(tariffName);
        if (commissionRate == null) {
            return "0.05"; // Default commission rate if not found
        }
        return String.valueOf(commissionRate / 100);
    }
}


