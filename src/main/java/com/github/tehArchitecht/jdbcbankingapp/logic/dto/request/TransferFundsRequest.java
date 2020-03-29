package com.github.tehArchitecht.jdbcbankingapp.logic.dto.request;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TransferFundsRequest {
    private UUID senderAccountId;
    private String receiverPhoneNumber;
    private BigDecimal amount;
    private Currency currency;
}
