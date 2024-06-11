package ru.ssu.springproject.stocks_trading.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssu.springproject.stocks_trading.models.Tariff;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
//    Double findCommissionPercentageByName(String tariff);

    @Query("SELECT t.commissionPercentage FROM Tariff t WHERE t.name = :name")
    Double findCommissionPercentageByName(@Param("name") String name);
}