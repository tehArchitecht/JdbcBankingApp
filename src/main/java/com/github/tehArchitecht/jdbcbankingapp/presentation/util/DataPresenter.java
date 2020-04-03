package com.github.tehArchitecht.jdbcbankingapp.presentation.util;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.response.AccountDto;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.response.OperationDto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

/**
 * Provides textual representation for various objects displayed by the BankTui
 * class (it's main purpose is to display operation tables).
 */
public class DataPresenter {
    private static final int UUID_LENGTH = 36;
    private static final int SUBSTRING_LENGTH = 12;
    private static final int BEGIN_INDEX = UUID_LENGTH - SUBSTRING_LENGTH;

    private static final String newLine = String.format("%n");

    public static String accountToString(AccountDto account) {
        String base = String.format(
                "%s (%s %s)",
                uuidToString(account.getId()),
                fundsToString(account.getBalance()),
                currencyToString(account.getCurrency())
        );
        return account.isPrimary() ? base + " [основной]" : base;
    }

    public static String accountListToString(List<AccountDto> accounts) {
        StringBuilder result = new StringBuilder();

        int size = accounts.size();
        for (int i = 1; i < size; i++)
            result.append(i).append(". ").append(accountToString(accounts.get(i - 1))).append(newLine);
        result.append(size).append(". ").append(accountToString(accounts.get(size - 1)));

        return result.toString();
    }

    public static String operationListToString(List<OperationDto> operations) {
        final int NUM_COLUMNS = 7;
        final int NUM_ROWS = operations.size();

        String[][] columns = new String[NUM_COLUMNS][NUM_ROWS];
        int[] columnWidths = new int[NUM_COLUMNS];
        String[] row = new String[NUM_COLUMNS];

        for (int r = 0; r < NUM_ROWS; r++) {
            OperationDto operation = operations.get(r);

            row[0] = timestampToString(operation.getDate());
            row[1] = currencyToString(operation.getCurrency());
            row[2] = uuidToString(operation.getSenderAccountId());
            row[3] = uuidToString(operation.getReceiverAccountId());
            row[4] = fundsToString(operation.getAmount());
            row[5] = fundsToString(operation.getInitialBalance());
            row[6] = fundsToString(operation.getResultingBalance());

            for (int c = 0; c < NUM_COLUMNS; c++) {
                String cell = row[c];
                columns[c][r] = cell;
                if (cell.length() > columnWidths[c])
                    columnWidths[c] = cell.length();
            }
        }

        String[] headerColumns = new String[] {
                "Дата",
                "Валюта",
                "Счёт отправителя",
                "Счёт получателя",
                "Сумма",
                "Баланс до перевода",
                "Баланс после перевода"
        };

        for (int c = 0; c < NUM_COLUMNS; c++) {
            String cell = headerColumns[c];
            if (cell.length() > columnWidths[c])
                columnWidths[c] = cell.length();
        }

        StringBuilder rule   = new StringBuilder("+");
        StringBuilder header = new StringBuilder("|");
        for (int c = 0; c < NUM_COLUMNS; c++) {
            String cell = headerColumns[c];
            int width = columnWidths[c];
            rule.append(new String(new char[width]).replace('\0', '-')).append("+");
            header.append(String.format("%" + width + "s|", cell));
        }

        StringBuilder table = new StringBuilder();
        table.append(rule).append(newLine).append(header).append(newLine).append(rule);

        for (int r = 0; r < NUM_ROWS; r++) {
            table.append(newLine).append("|");
            for (int c = 0; c < NUM_COLUMNS; c++) {
                String cell = columns[c][r];
                int width = columnWidths[c];
                table.append(String.format("%" + width + "s|", cell));
            }
        }

        table.append(newLine).append(rule);

        return table.toString();
    }

    private static String currencyToString(Currency currency) {
        return currency.toString().toLowerCase();
    }

    private static String fundsToString(BigDecimal funds) {
        return new DecimalFormat("0.000").format(funds);
    }

    private static String uuidToString(UUID uuid) {
        return "..." + uuid.toString().substring(BEGIN_INDEX);
    }

    private static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timestamp);
    }
}
