package ru.ssu.springproject.stocks_trading.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssu.springproject.stocks_trading.exceptions.NotEnoughQuantityException;
import ru.ssu.springproject.stocks_trading.models.Stock;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.models.UserStock;
import ru.ssu.springproject.stocks_trading.repositories.StockRepository;
import ru.ssu.springproject.stocks_trading.repositories.UserRepository;
import ru.ssu.springproject.stocks_trading.repositories.UserStockRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final StockRepository stockRepository;

    @Transactional
    public void sellProduct(User user, UserStock userStock, int quantity) {
        log.info("Starting the sellProduct method for user: {}, stock: {}, quantity: {}", user.getUsername(), userStock.getStockName(), quantity);

        if (userStock.getQuantity() < quantity) {
            throw new NotEnoughQuantityException("Not enough stocks to sell");
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        double commissionRate = getCommissionRate(user.getTariff());
        double totalCost = userStock.getAveragePrice() * quantity * (1 - commissionRate);
        log.info("Total cost for selling stock: {}, quantity: {}, at price: {}, with commission: {} is {}", userStock.getStockName(), quantity, userStock.getAveragePrice(), commissionRate, totalCost);

        user.setBalance(user.getBalance() + totalCost);
        userRepository.save(user);

        userStock.setQuantity(userStock.getQuantity() - quantity);
        if (userStock.getQuantity() == 0) {
            userStockRepository.deleteById(userStock.getId());
            log.info("UserStock for user: {} with stock: {} has been deleted as quantity is 0", user.getUsername(), userStock.getStockName());
        } else {
            userStockRepository.save(userStock);
            log.info("Updated UserStock for user: {} with stock: {}, new quantity: {}", user.getUsername(), userStock.getStockName(), userStock.getQuantity());
        }

        Stock stock = stockRepository.findByCompanyName(userStock.getStockName());
        if (stock == null) {
            stock = new Stock();
            stock.setAvailableQuantity(quantity);
            stock.setCompanyName(userStock.getStockName());
            stock.setSellPrice(userStock.getAveragePrice());
            stock.setBuyPrice(userStock.getAveragePrice() * 1.03);
            log.info("New stock created for company: {}, quantity: {}, sell price: {}, buy price: {}", userStock.getStockName(), quantity, userStock.getAveragePrice(), userStock.getAveragePrice() * 1.03);
        } else {
            stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
            log.info("Updated stock for company: {}, new available quantity: {}", stock.getCompanyName(), stock.getAvailableQuantity());
        }
        stockRepository.save(stock);
        log.info("User {} sold stock {}, quantity: {}, total amount: {}, commission rate: {}%", user.getUsername(), userStock.getStockName(), quantity, totalCost, commissionRate * 100);
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
