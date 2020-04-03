package com.github.tehArchitecht.jdbcbankingapp.presentation.util;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Provides uniform methods for reading objects used by the TUI from an
 * InputStream object.
 */
public class DataReader {
    private final Scanner in;

    public DataReader(InputStream inputStream) {
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
