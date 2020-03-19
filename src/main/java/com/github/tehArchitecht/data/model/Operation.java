package com.github.tehArchitecht.data.model;

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
}

