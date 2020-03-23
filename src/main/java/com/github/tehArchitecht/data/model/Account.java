package com.github.tehArchitecht.data.model;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account {
    private UUID id;
    private Long number;
    private Long userId;
    private BigDecimal balance;
    private Currency currency;

    public Account(Long userId, Currency currency) {
        this.userId = userId;
        this.currency = currency;
        this.balance = new BigDecimal("0.000");
    }
}

