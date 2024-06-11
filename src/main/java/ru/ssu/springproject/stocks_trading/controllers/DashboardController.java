package ru.ssu.springproject.stocks_trading.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ssu.springproject.stocks_trading.exceptions.InsufficientBalanceException;
import ru.ssu.springproject.stocks_trading.models.UserStock;
import ru.ssu.springproject.stocks_trading.repositories.UserRepository;
import ru.ssu.springproject.stocks_trading.repositories.UserStockRepository;
import ru.ssu.springproject.stocks_trading.services.DashboardService;
import ru.ssu.springproject.stocks_trading.services.UserService;
import ru.ssu.springproject.stocks_trading.models.User;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/dashboard")
public class DashboardController {
    private final UserService userService;
    private final DashboardService dashboardService;
    private final UserStockRepository userStockRepository;

    @GetMapping
    public String showUserDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Request to show user dashboard for user: {}", userDetails.getUsername());
        User user = userService.findByUsername(userDetails.getUsername());
        log.info("User details retrieved: {}", user);

        model.addAttribute("user", user);
        model.addAttribute("name", user.getUsername());
        model.addAttribute("stocks", user.getStocks());

        return "dashboard";
    }

    @PostMapping("/sell-stock")
    @ResponseBody
    public ResponseEntity<?> sellStock(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String stockName, @RequestParam int quantity) {
        log.info("Request to sell stock for user: {}, stockName: {}, quantity: {}", userDetails.getUsername(), stockName, quantity);
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            UserStock stock = userStockRepository.findByUserAndStockName(user, stockName);
            dashboardService.sellProduct(user, stock, quantity);
            log.info("Stock sold successfully for user: {}, stockName: {}, quantity: {}", userDetails.getUsername(), stockName, quantity);
            return ResponseEntity.ok().body("Stock sold successfully!");
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }
}

