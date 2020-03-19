package com.github.tehArchitecht.presentation;

import com.github.tehArchitecht.data.model.Currency;
import com.github.tehArchitecht.logic.dto.AccountDto;
import com.github.tehArchitecht.logic.dto.OperationDto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class DataPresenter {
    private static final int UUID_LENGTH = 36;
    private static final int SUBSTRING_LENGTH = 12;
    private static final int BEGIN_INDEX = UUID_LENGTH - SUBSTRING_LENGTH;

    private static final String newLine = String.format("%n|");

    public static String accountToString(AccountDto account) {
        return String.format(
                "%s (%s %s)",
                uuidToString(account.getId()),
                fundsToString(account.getBalance()),
                currencyToString(account.getCurrency())
        );
    }

    public static String accountListToString(List<AccountDto> accounts) {
        String result = "";
        for (int i = 1; i <= accounts.size(); i++)
            result += i + ". " + accountToString(accounts.get(i-1)) + newLine;
        return result;
    }

    public static String operationListToString(List<OperationDto> operations) {
        final int NUM_COLUMNS = 7;
        final int NUM_ROWS = operations.size();

        String[][] columns = new String[NUM_COLUMNS][NUM_ROWS];
        int columnWidths[] = new int[NUM_COLUMNS];
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

        String rule   = "+";
        String header = "|";
        for (int c = 0; c < NUM_COLUMNS; c++) {
            String cell = headerColumns[c];
            int width = columnWidths[c];
            rule += new String(new char[width]).replace('\0', '-') + "+";
            header += String.format("%"+width+"s|", cell);
        }

        String table = "";
        table += rule + newLine + header + newLine + rule;

        for (int r = 0; r < NUM_ROWS; r++) {
            table += newLine;
            for (int c = 0; c < NUM_COLUMNS; c++) {
                String cell = columns[c][r];
                int width = columnWidths[c];
                table += String.format("%"+width+"s|", cell);
            }
        }

        table += newLine + rule;

        return table;
    }

    private static String currencyToString(Currency currency) {
        return currency.toString().toLowerCase();
    }

    private static String fundsToString(BigDecimal funds) {
        return funds.toString();
    }

    private static String uuidToString(UUID uuid) {
        return "..." + uuid.toString().substring(BEGIN_INDEX);
    }

    private static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timestamp);
    }
}
