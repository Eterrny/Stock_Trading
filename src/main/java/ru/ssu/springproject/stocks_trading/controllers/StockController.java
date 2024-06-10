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
import ru.ssu.springproject.stocks_trading.models.Stock;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.repositories.StockRepository;
import ru.ssu.springproject.stocks_trading.services.StockService;
import ru.ssu.springproject.stocks_trading.services.UserService;

import java.security.Principal;
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

    @PostMapping("/buy-stock")
    @ResponseBody
    public ResponseEntity<?> buyStock(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String company, @RequestParam int quantity) {
        try {
            Stock stock = stockRepository.findByCompanyName(company).get(0);
            log.info("ТУТ");
            stockService.buyProduct(userDetails, stock, quantity);
            log.error("ТУТ");
            return ResponseEntity.ok().body("Stock purchased successfully!");
        } catch (StockService.InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

//    @PostMapping("/buy-stock")
//    public String buyStock(@AuthenticationPrincipal Principal principal, Stock stock, int quantity) {
//        stockService.buyProduct(principal, stock, quantity);
//        return "redirect:/trading";
//    }

}
