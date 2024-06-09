package ru.ssu.springproject.stocks_trading.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssu.springproject.stocks_trading.models.Stock;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByCompanyName(String companyName);
}

