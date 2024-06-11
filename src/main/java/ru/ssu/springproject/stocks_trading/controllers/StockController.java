package ru.ssu.springproject.stocks_trading.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.ssu.springproject.stocks_trading.exceptions.InsufficientBalanceException;
import ru.ssu.springproject.stocks_trading.models.Stock;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.repositories.StockRepository;
import ru.ssu.springproject.stocks_trading.services.StockService;
import ru.ssu.springproject.stocks_trading.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;
    private final StockRepository stockRepository;
    private final UserService userService;

    @GetMapping("/trading")
    public String showTradingPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Request to show trading page for user: {}", userDetails.getUsername());
        List<Stock> stocks = findAllWithNotNullQty();
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("stocks", stocks);
        model.addAttribute("user", user);
        return "trading";
    }

    @PostMapping("/buy-stock")
    @ResponseBody
    public ResponseEntity<?> buyStock(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String company, @RequestParam int quantity) {
        log.info("Request to buy stock for user: {}, company: {}, quantity: {}", userDetails.getUsername(), company, quantity);
        try {
            Stock stock = stockRepository.findByCompanyName(company);
            stockService.buyProduct(userDetails, stock, quantity);
            log.info("Stock purchased successfully for user: {}, company: {}, quantity: {}", userDetails.getUsername(), company, quantity);
            return ResponseEntity.ok().body("Stock purchased successfully!");
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    private List<Stock> findAllWithNotNullQty() {
        return stockRepository.findAll().stream()
                .filter(stock -> stock.getAvailableQuantity() != 0)
                .collect(Collectors.toList());
    }
}
