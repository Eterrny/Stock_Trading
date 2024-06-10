package ru.ssu.springproject.stocks_trading.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssu.springproject.stocks_trading.models.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByCompanyName(String companyName);
}