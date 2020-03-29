package com.github.tehArchitecht.jdbcbankingapp.data.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Account {
    private UUID id;
    private Long userId;
    private BigDecimal balance;
    private Currency currency;

    public Account(Long userId, Currency currency) {
        this.userId = userId;
        this.currency = currency;
        this.balance = new BigDecimal("0.000");
    }
}

