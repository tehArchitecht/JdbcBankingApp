package com.github.tehArchitecht.jdbcbankingapp.presentation;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Scanner;

public class InputReader {
    private final Scanner in;

    public InputReader(InputStream inputStream) {
        in = new Scanner(inputStream);
    }

    public String readLine() {
        return in.nextLine();
    }

    public BigDecimal readBigDecimal() throws NumberFormatException {
        return new BigDecimal(readLine());
    }

    public int readInt() throws NumberFormatException {
        return Integer.parseInt(readLine());
    }

    public Currency readCurrency() throws IllegalArgumentException {
        return Currency.valueOf(readLine().trim().toUpperCase());
    }
}
