package com.github.tehArchitecht.logic.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.github.tehArchitecht.data.model.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID id;
    private BigDecimal balance;
    private Currency currency;
}

