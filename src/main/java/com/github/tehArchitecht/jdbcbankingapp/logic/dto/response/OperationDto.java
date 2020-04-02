package com.github.tehArchitecht.jdbcbankingapp.logic.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class OperationDto {
    private Timestamp date;
    private Currency currency;
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private BigDecimal amount;
    private BigDecimal initialBalance;
    private BigDecimal resultingBalance;
}