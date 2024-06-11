package ru.ssu.springproject.stocks_trading.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "stocks")
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "buy_price", nullable = false)
    private Double buyPrice;
    @Column(name = "sell_price", nullable = false)
    private Double sellPrice;
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;
}