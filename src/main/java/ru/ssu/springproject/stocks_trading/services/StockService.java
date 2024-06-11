package ru.ssu.springproject.stocks_trading.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ssu.springproject.stocks_trading.exceptions.InsufficientBalanceException;
import ru.ssu.springproject.stocks_trading.exceptions.NotEnoughQuantityException;
import ru.ssu.springproject.stocks_trading.models.Stock;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.models.UserStock;
import ru.ssu.springproject.stocks_trading.repositories.StockRepository;
import ru.ssu.springproject.stocks_trading.repositories.UserRepository;
import ru.ssu.springproject.stocks_trading.repositories.UserStockRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;

    public void buyProduct(UserDetails userDetails, Stock stock, int quantity) {
        log.info("Request to buy stock: {}, quantity: {} for user: {}", stock.getCompanyName(), quantity, userDetails.getUsername());

        if (stock.getAvailableQuantity() < quantity) {
            log.warn("Not enough stock available to buy: requested {}, available {}", quantity, stock.getAvailableQuantity());
            throw new NotEnoughQuantityException("Not enough stock to buy");
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        double commissionRate = getCommissionRate(user.getTariff());
        double totalCost = stock.getBuyPrice() * quantity * (1 + commissionRate);
        log.info("Total cost calculated: {}, commission rate: {}%", totalCost, commissionRate * 100);

        if (user.getBalance() >= totalCost) {
            user.setBalance(user.getBalance() - totalCost);
            userRepository.save(user);
            log.info("User balance after purchase: {}", user.getBalance());

            stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
            stockRepository.save(stock);
            log.info("Stock quantity updated: {}", stock.getAvailableQuantity());

            UserStock userStock = userStockRepository.findByUserAndStockName(user, stock.getCompanyName());
            if (userStock == null) {
                userStock = new UserStock();
                userStock.setUser(user);
                userStock.setStockName(stock.getCompanyName());
                userStock.setQuantity(quantity);
                userStock.setAveragePrice(stock.getSellPrice());
                log.info("New user stock created: {}", userStock);
            } else {
                int newQuantity = userStock.getQuantity() + quantity;
                double newAveragePrice = (stock.getSellPrice());
                userStock.setQuantity(newQuantity);
                userStock.setAveragePrice(newAveragePrice);
                log.info("Updated user stock: {}", userStock);
            }
            userStockRepository.save(userStock);
            log.info("User {} bought stock {}, quantity: {}, total cost: {}, commission rate: {}%", user.getUsername(), stock.getCompanyName(), quantity, totalCost, commissionRate * 100);
        } else {
            log.warn("Insufficient balance for user: {} to complete the purchase. Required: {}, available: {}", user.getUsername(), totalCost, user.getBalance());
            throw new InsufficientBalanceException("Insufficient balance to complete the purchase");
        }
    }

    private double getCommissionRate(String tariff) {
        return switch (tariff.toLowerCase()) {
            case "premium" -> 0.005;
            case "pro" -> 0.01;
            case "novice" -> 0.03;
            default -> 0.05;
        };
    }
}
