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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;

    public void buyProduct(UserDetails userDetails, Stock stock, int quantity) {
        if (stock.getAvailableQuantity() < quantity) {
            throw new NotEnoughQuantityException("Not enough stock to buy");
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        double commissionRate = getCommissionRate(user.getTariff());
        double totalCost = stock.getBuyPrice() * quantity * (1 + commissionRate);
        if (user.getBalance() >= totalCost) {
            user.setBalance(user.getBalance() - totalCost);
            userRepository.save(user);

            stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
            stockRepository.save(stock);

            UserStock userStock = userStockRepository.findByUserAndStockName(user, stock.getCompanyName());
            if (userStock == null) {
                userStock = new UserStock();
                userStock.setUser(user);
                userStock.setStockName(stock.getCompanyName());
                userStock.setQuantity(quantity);
                userStock.setAveragePrice(stock.getSellPrice());
            } else {
                int newQuantity = userStock.getQuantity() + quantity;
                double newAveragePrice = (stock.getSellPrice());
                userStock.setQuantity(newQuantity);
                userStock.setAveragePrice(newAveragePrice);
            }
            userStockRepository.save(userStock);
            log.info("Пользователь {} купил акцию {}, количество: {} стоимость: {}, процент комиссии: {}%", user.getUsername(), stock.getCompanyName(), quantity, totalCost, commissionRate * 100);
        } else {
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
