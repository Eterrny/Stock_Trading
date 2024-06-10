package ru.ssu.springproject.stocks_trading.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.ssu.springproject.stocks_trading.models.Stock;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.repositories.StockRepository;
import ru.ssu.springproject.stocks_trading.services.StockService;
import ru.ssu.springproject.stocks_trading.services.UserService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;
    private final StockRepository stockRepository;
    private final UserService userService;

    @GetMapping("/trading")
    public String showTradingPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<Stock> stocks = stockRepository.findAll();
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("stocks", stocks);
        model.addAttribute("user", user);
        return "trading";
    }
}
