package ru.ssu.springproject.stocks_trading.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssu.springproject.stocks_trading.repositories.UserRepository;
import ru.ssu.springproject.stocks_trading.services.UserService;
import ru.ssu.springproject.stocks_trading.models.User;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/dashboard")
public class DashboardController {
    private final UserService userService;

    @GetMapping
    public String showUserDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        log.info("\n{}", user.toString());
        log.info("\n{}", user.getUsername());
        log.info("\n{}", user.getTariff());
        log.info("\n{}", user.getBalance());
//        model.addAttribute("username", user.getUsername());
//        model.addAttribute("balance", user.getBalance());
//        model.addAttribute("tariff", user.getTariff());
        model.addAttribute("user", user);
        model.addAttribute("name", user.getUsername());
        return "dashboard";
    }
}

