package ru.ssu.springproject.stocks_trading.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.models.UserStock;

import java.util.List;

public interface UserStockRepository extends JpaRepository<UserStock, Long> {
    List<UserStock> findByUser(User user);

    UserStock findByUserAndStockName(User user, String stockName);
}