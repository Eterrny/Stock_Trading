package ru.ssu.springproject.stocks_trading.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "tariffs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "tariff")
    private String name;
    @Column(name = "commission")
    private Double commissionPercentage;
}