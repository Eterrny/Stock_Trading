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
        if (userStock.getQuantity() < quantity) {
            throw new NotEnoughQuantityException("Not enough stocks to sell");
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        double commissionRate = getCommissionRate(user.getTariff());
        double totalCost = userStock.getAveragePrice() * quantity * (1 - commissionRate);

        user.setBalance(user.getBalance() + totalCost);
        userRepository.save(user);

        userStock.setQuantity(userStock.getQuantity() - quantity);
        if (userStock.getQuantity() == 0) {
            userStockRepository.deleteById(userStock.getId());
        } else {
            userStockRepository.save(userStock);
        }

        Stock stock = stockRepository.findByCompanyName(userStock.getStockName());
        if (stock == null) {
            stock = new Stock();
            stock.setAvailableQuantity(quantity);
            stock.setCompanyName(userStock.getStockName());
            stock.setSellPrice(userStock.getAveragePrice());
            stock.setBuyPrice(userStock.getAveragePrice() * 1.03);
        } else {
            stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
        }
        stockRepository.save(stock);
        log.info("Пользователь {} продал акцию {}, количество: {} сумма: {}, процент комиссии: {}%", user.getUsername(), userStock.getStockName(), quantity, totalCost, commissionRate * 100);
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
