package com.github.tehArchitecht.jdbcbankingapp.data.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Operation {
    private Long id;
    private Timestamp date;
    private Currency currency;
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private BigDecimal amount;
    private BigDecimal senderInitialBalance;
    private BigDecimal senderResultingBalance;
    private BigDecimal receiverInitialBalance;
    private BigDecimal receiverResultingBalance;

    public Operation(Timestamp date, Currency currency, UUID senderAccountId, UUID receiverAccountId, BigDecimal amount,
                     BigDecimal senderInitialBalance, BigDecimal senderResultingBalance,
                     BigDecimal receiverInitialBalance, BigDecimal receiverResultingBalance) {
        this.date = date;
        this.currency = currency;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.senderInitialBalance = senderInitialBalance;
        this.senderResultingBalance = senderResultingBalance;
        this.receiverInitialBalance = receiverInitialBalance;
        this.receiverResultingBalance = receiverResultingBalance;
    }
}

