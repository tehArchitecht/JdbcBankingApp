package com.github.tehArchitecht.logic;

import com.github.tehArchitecht.data.model.Currency;

import java.math.BigDecimal;
import java.math.MathContext;

public class CurrencyConverter {
    private static final BigDecimal EUR_TO_RUB_CONVERSION_MULTIPLIER = new BigDecimal(80.93);
    private static final BigDecimal USD_TO_RUB_CONVERSION_MULTIPLIER = new BigDecimal(72.62);

    private static final int PRECISION = 3;
    private static final int MODE = BigDecimal.ROUND_HALF_UP;

    public static BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        BigDecimal multiplier = new BigDecimal(1);

        if (from == Currency.EUR) multiplier = multiplier.multiply(EUR_TO_RUB_CONVERSION_MULTIPLIER);
        else if (from == Currency.USD) multiplier = multiplier.multiply(USD_TO_RUB_CONVERSION_MULTIPLIER);

        if (to == Currency.EUR) multiplier = multiplier.divide(EUR_TO_RUB_CONVERSION_MULTIPLIER, PRECISION, MODE);
        else if (to == Currency.USD) multiplier = multiplier.divide(USD_TO_RUB_CONVERSION_MULTIPLIER, PRECISION, MODE);

        return amount.multiply(multiplier).round(new MathContext(PRECISION));
    }
}
