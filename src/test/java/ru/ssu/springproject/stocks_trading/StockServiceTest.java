package ru.ssu.springproject.stocks_trading;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ssu.springproject.stocks_trading.exceptions.InsufficientBalanceException;
import ru.ssu.springproject.stocks_trading.exceptions.NotEnoughQuantityException;
import ru.ssu.springproject.stocks_trading.exceptions.TradingPlatformClosedException;
import ru.ssu.springproject.stocks_trading.models.*;
import ru.ssu.springproject.stocks_trading.repositories.*;
import ru.ssu.springproject.stocks_trading.services.StockService;

import java.time.LocalTime;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStockRepository userStockRepository;

    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private StockService stockService;

    private User user;
    private UserDetails userDetails;
    private Stock stock;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("testuser");
        user.setBalance(1000.0);
        user.setTariff("standard");

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        stock = new Stock();
        stock.setCompanyName("TestStock");
        stock.setSellPrice(50.0);
        stock.setBuyPrice(50.0);
        stock.setAvailableQuantity(100);
    }

    @Test
    public void testBuyProduct_NotEnoughQuantity() {
        stock.setAvailableQuantity(5);
        assertThrows(NotEnoughQuantityException.class, () -> {
            stockService.buyProduct(userDetails, stock, 10);
        });
    }

    @Test
    public void testBuyProduct_InsufficientBalance() {
        user.setBalance(100.0);
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(tariffRepository.findCommissionPercentageByName("standard")).thenReturn(0.05);

        assertThrows(InsufficientBalanceException.class, () -> {
            stockService.buyProduct(userDetails, stock, 10);
        });
    }

    @Test
    public void testBuyProduct_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(tariffRepository.findCommissionPercentageByName("standard")).thenReturn(0.05);

        stockService.buyProduct(userDetails, stock, 10);

        verify(userRepository, times(1)).save(user);
        verify(stockRepository, times(1)).save(stock);
        verify(userStockRepository, times(1)).save(any(UserStock.class));
    }

    @Test
    public void testBuyProduct_OutsideWorkingHours() {
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        LocalTime currentTime = LocalTime.of(23, 0);
        mockStatic(LocalTime.class);
        ZoneId zoneId = ZoneId.of("UTC+3");
        when(LocalTime.now(zoneId)).thenReturn(currentTime);
        when(LocalTime.of(10, 0)).thenReturn(startTime);
        when(LocalTime.of(22, 0)).thenReturn(endTime);

        assertThrows(TradingPlatformClosedException.class, () -> {
            stockService.buyProduct(userDetails, stock, 10);
        });
    }
}