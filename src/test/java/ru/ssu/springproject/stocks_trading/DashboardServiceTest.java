package ru.ssu.springproject.stocks_trading;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.ssu.springproject.stocks_trading.exceptions.NotEnoughQuantityException;
import ru.ssu.springproject.stocks_trading.exceptions.TradingPlatformClosedException;
import ru.ssu.springproject.stocks_trading.models.Stock;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.models.UserStock;
import ru.ssu.springproject.stocks_trading.repositories.StockRepository;
import ru.ssu.springproject.stocks_trading.repositories.UserRepository;
import ru.ssu.springproject.stocks_trading.repositories.UserStockRepository;
import ru.ssu.springproject.stocks_trading.repositories.TariffRepository;
import ru.ssu.springproject.stocks_trading.services.DashboardService;

import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class DashboardServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserStockRepository userStockRepository;

    @Mock
    StockRepository stockRepository;

    @Mock
    TariffRepository tariffRepository;

    @InjectMocks
    DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSellProduct() {
        User user = new User();
        user.setUsername("testUser");
        user.setBalance(1000.0);

        UserStock userStock = new UserStock();
        userStock.setId(1L);
        userStock.setStockName("TestStock");
        userStock.setAveragePrice(50.0);
        userStock.setQuantity(20);

        Stock stock = new Stock();
        stock.setCompanyName("TestStock");
        stock.setAvailableQuantity(100);

        when(userRepository.save(any())).thenReturn(user);
        when(userStockRepository.save(any())).thenReturn(userStock);
        when(stockRepository.findByCompanyName(anyString())).thenReturn(stock);
        when(tariffRepository.findCommissionPercentageByName(anyString())).thenReturn(0.05);

        dashboardService.sellProduct(user, userStock, 10);

        verify(userRepository, times(1)).save(any());
        verify(userStockRepository, times(1)).save(any());
        verify(stockRepository, times(1)).save(any());
    }

    @Test
    void testSellProduct_NotEnoughQuantityException() {
        User user = new User();
        user.setUsername("testUser");
        user.setBalance(1000.0);

        UserStock userStock = new UserStock();
        userStock.setId(1L);
        userStock.setStockName("TestStock");
        userStock.setAveragePrice(50.0);
        userStock.setQuantity(20);

        NotEnoughQuantityException exception = assertThrows(NotEnoughQuantityException.class,
                () -> dashboardService.sellProduct(user, userStock, 30));

        assert(exception.getMessage().contains("Not enough stocks to sell"));
    }

    @Test
    public void testBuyProduct_OutsideWorkingHours() {
        User user = new User();
        user.setUsername("testUser");
        user.setBalance(1000.0);

        UserStock userStock = new UserStock();
        userStock.setId(1L);
        userStock.setStockName("TestStock");
        userStock.setAveragePrice(50.0);
        userStock.setQuantity(20);

        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        LocalTime currentTime = LocalTime.of(23, 0);
        mockStatic(LocalTime.class);
        ZoneId zoneId = ZoneId.of("UTC+3");
        when(LocalTime.now(zoneId)).thenReturn(currentTime);
        when(LocalTime.of(10, 0)).thenReturn(startTime);
        when(LocalTime.of(22, 0)).thenReturn(endTime);

        assertThrows(TradingPlatformClosedException.class, () -> {
            dashboardService.sellProduct(user, userStock, 10);
        });
    }
}
