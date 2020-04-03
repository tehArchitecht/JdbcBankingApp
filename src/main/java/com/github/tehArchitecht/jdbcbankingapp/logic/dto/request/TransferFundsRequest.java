package com.github.tehArchitecht.jdbcbankingapp.logic.dto.request;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TransferFundsRequest {
    @NotNull
    private UUID senderAccountId;
    @NotNull
    @NotEmpty
    private String receiverPhoneNumber;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    private Currency currency;
}
