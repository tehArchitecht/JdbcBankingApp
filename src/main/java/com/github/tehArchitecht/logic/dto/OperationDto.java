package com.github.tehArchitecht.logic.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
public class OperationDto {
    private Timestamp date;
    private Currency currency;
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private BigDecimal amount;
    private BigDecimal initialBalance;
    private BigDecimal resultingBalance;
}