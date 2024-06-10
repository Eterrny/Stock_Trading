package ru.ssu.springproject.stocks_trading.services;


import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ssu.springproject.stocks_trading.models.Stock;
import ru.ssu.springproject.stocks_trading.repositories.StockRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public List<Stock> findByCompanyName(String companyName) {
        if (companyName == null) {
            return stockRepository.findAll();
        }
        return stockRepository.findByCompanyName(companyName);
    }


}
