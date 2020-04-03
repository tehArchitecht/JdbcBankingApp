package com.github.tehArchitecht.jdbcbankingapp.logic.dto.request;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DepositFundsRequest {
    @NotNull
    private UUID accountId;
    @NotNull
    private Currency currency;
    @NotNull
    @Positive
    private BigDecimal amount;
}
