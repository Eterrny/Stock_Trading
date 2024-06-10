package ru.ssu.springproject.stocks_trading.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="user_stocks")
@Data
public class UserStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "average_price", nullable = false)
    private Double averagePrice;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn
    private User user;
}

