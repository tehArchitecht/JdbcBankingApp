package com.github.tehArchitecht.jdbcbankingapp.presentation;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;
import com.github.tehArchitecht.jdbcbankingapp.logic.Result;
import com.github.tehArchitecht.jdbcbankingapp.logic.Status;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.AccountDto;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.OperationDto;
import com.github.tehArchitecht.jdbcbankingapp.logic.security.SecurityToken;
import com.github.tehArchitecht.jdbcbankingapp.logic.service.BankService;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * A TUI for the BankService class.
 */
public class BankTui {
    private final static Logger logger = Logger.getLogger(BankTui.class);

    private final InputReader in;
    private final PrintStream out;

    private final BankService bankService;

    private SecurityToken token = null;
    private boolean running;

    public BankTui(InputStream in, PrintStream out) {
        this.in = new InputReader(in);
        this.out = out;
        this.bankService = new BankService();
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Primary screens (methods)                                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

    public void run() {
        out.println("БАНКОВСКОЕ ПРИЛОЖЕНИЕ");

        running = true;
        while (running) {
            out.println();
            if (token == null)
                unauthorizedScreen();
            else
                authorizedScreen();
        }
    }

    private void unauthorizedScreen() {
        out.println("Доступные действия:");
        out.println("1. Зарегистрироваться в системе");
        out.println("2. Войти в систему");
        out.println("3. Выйти из программы");
        out.print("Введите номер (1, 2, 3): ");

        int optionNumber = inputOptionNumber(3);

        out.println();
        switch (optionNumber) {
            case 1: signUp(); break;
            case 2: signIn(); break;
            case 3: running = false; break;
        }
    }

    private void authorizedScreen() {
        out.println("Доступные действия:");
        out.println("1. Создать счёт");
        out.println("2. Внести средства");
        out.println("3. Перевести средства");
        out.println("4. Просмотреть историю операций");
        out.println("5. Просмотреть привязанные счета");
        out.println("6. Выйти из системы (аккаунта)");
        out.println("7. Выйти из программы");
        out.print("Введите номер (1, 2, 3, 4, 5, 6, 7): ");

        int optionNumber = inputOptionNumber(7);

        out.println();
        switch (optionNumber) {
            case 1: createAccount(); break;
            case 2: depositFunds(); break;
            case 3: transferFunds(); break;
            case 4: displayOperationHistory(); break;
            case 5: displayUserAccounts(); break;
            case 6: signOut(); break;
            case 7: running = false; break;
        }
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Screens for unauthorized users                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    private void signUp() {
        out.println("-- Регистрация --");

        out.print("Введите логин: ");
        String userName = inputUserName();

        Result<Boolean> result = bankService.isNameInUse(userName);
        if (handleFailedResult(result)) return;

        boolean nameTaken = result.getData();
        if (nameTaken) {
            out.println("Ошибка. Пользователь с таким именем уже существует.");
        } else {
            out.print("Введите пароль: ");
            String password = inputPassword();

            out.print("Введите телефон: ");
            String phoneNumber = inputPhoneNumber();

            out.print("Введите адрес: ");
            String address = inputAddress();

            Status status = bankService.signUp(userName, password, address, phoneNumber);
            displayStatus(status);
        }
    }

    private void signIn() {
        out.println("Доступные варианты:");
        out.println("1. Войти по логину");
        out.println("2. Войти по номеру телефона");
        out.print("Введите номер (1, 2): ");

        int optionNumber = inputOptionNumber(2);

        out.println();
        switch (optionNumber) {
            case 1: signInWithName(); break;
            case 2: signInWithPhoneNumber(); break;
        }
    }

    private void signInWithName() {
        out.println("-- Вход по логину --");

        out.print("Введите логин: ");
        String userName = inputUserName();

        out.print("Введите пароль: ");
        String password = inputPassword();

        Result<SecurityToken> result = bankService.signInWithName(userName, password);
        if (handleFailedResult(result)) return;

        token = result.getData();
        out.println("Вы успешно вошли в систему.");
    }

    private void signInWithPhoneNumber() {
        out.println("-- Вход по номеру телефона --");

        out.print("Введите номер телефона: ");
        String phoneNumber = inputPhoneNumber();

        out.print("Введите пароль: ");
        String password = inputPassword();

        Result<SecurityToken> result = bankService.signWithPhoneNumber(phoneNumber, password);
        if (handleFailedResult(result)) return;

        token = result.getData();
        out.println("Вы успешно вошли в систему.");
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Screens for authorized users                                                                                   //
    // -------------------------------------------------------------------------------------------------------------- //

    private void createAccount() {
        out.println("-- Создание счёта --");

        out.print("Введите валюту (EUR, RUB, USD): ");
        Currency currency = inputCurrency();

        Status status = bankService.createAccount(token, currency);
        displayStatus(status);
    }

    private void depositFunds() {
        out.println("-- Пополнение счёта --");

        UUID accountId = selectAccount("Выберите счёт: ");
        if (accountId == null) return;

        out.print("Введите валюту (EUR, RUB, USD): ");
        Currency currency = inputCurrency();

        out.print("Введите сумму: ");
        BigDecimal amount = inputFunds();

        Status status = bankService.depositFunds(token, accountId, currency, amount);
        out.println(StatusMapper.statusToString(status));
    }

    private void transferFunds() {
        out.println("-- Перевод средств --");

        UUID accountId = selectAccount("Выберите счёт для списания средств: ");
        if (accountId == null) return;

        out.print("Введите номер телефона получателя: ");
        String phoneNumber = inputPhoneNumber();

        out.print("Введите валюту (EUR, RUB, USD): ");
        Currency currency = inputCurrency();

        out.print("Введите сумму: ");
        BigDecimal amount = inputFunds();

        Status status = bankService.transferFunds(token, accountId, phoneNumber, amount, currency);
        displayStatus(status);
    }

    private void displayOperationHistory() {
        out.println("-- Просмотр истории операций --");

        Result<List<OperationDto>> result = bankService.getUserOperations(token);
        if (handleFailedResult(result)) return;

        List<OperationDto> operations = result.getData();
        if (operations.isEmpty()) {
            out.println("История операций по вашему аккаунту пуста.");
        } else {
            out.println("Список операций по вашим счетам:");
            printOperations(operations);
        }
    }

    private void displayUserAccounts() {
        out.println("-- Просмотр счетов --");

        Result<List<AccountDto>> result = bankService.getUserAccounts(token);
        if (handleFailedResult(result)) return;

        List<AccountDto> accounts = result.getData();
        if (accounts.isEmpty()) {
            out.println("У вас нет ни одного счёта.");
        } else {
            out.println("Список ваших счетов:");
            printAccounts(accounts);
        }
    }

    private void signOut() {
        out.println("-- Выход из аккаунта --");

        bankService.signOut(token);
        token = null;
        out.println("Вы успешно вышли из системы.");
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Helper methods                                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    // Input methods ------------------------------------------------------------------------------------------------ //

    private String inputUserName() { return in.readLine(); }
    private String inputPhoneNumber() { return in.readLine(); }
    private String inputPassword() { return in.readLine(); }
    private String inputAddress() { return in.readLine(); }

    private UUID selectAccount(String message) {
        Result<List<AccountDto>> result = bankService.getUserAccounts(token);
        if (handleFailedResult(result)) return null;

        List<AccountDto> accounts = result.getData();
        if (accounts.isEmpty()) {
            out.println("Ошибка. У вас нет ни одного счета.");
            return null;
        }

        return accounts.size() == 1 ? accounts.get(0).getId() : selectAccount(accounts, message);
    }

    private UUID selectAccount(List<AccountDto> accounts, String message) {
        out.println(message);
        printAccounts(accounts);

        int count = accounts.size();
        out.print("Введите номер (число от 1 до " + count + "): ");

        int optionNumber = inputOptionNumber(count);
        return accounts.get(optionNumber-1).getId();
    }

    private int inputOptionNumber(int numOptions) {
        while (true) {
            try {
                int optionNumber = in.readInt();
                if (optionNumber >= 1 && optionNumber <= numOptions)
                    return optionNumber;
            } catch (NumberFormatException e) {
                logger.warn(e);
            }
            out.print("Ошибка. Введите целое число от 1 до " + numOptions + ": ");
        }
    }

    private Currency inputCurrency() {
        while (true) {
            try {
                return in.readCurrency();
            } catch (IllegalArgumentException e) {
                logger.warn(e);
            }
            out.print("Ошибка. Введите одно из значений EUR, RUB, USD: ");
        }
    }

    private BigDecimal inputFunds() {
        while (true) {
            try {
                return in.readBigDecimal();
            } catch (NumberFormatException e) {
                logger.warn(e);
            }
            out.print("Ошибка. Введите вещественное число: ");
        }
    }

    // Output methods ----------------------------------------------------------------------------------------------- //

    private void printAccounts(List<AccountDto> accounts) {
        out.println(DataPresenter.accountListToString(accounts));
    }

    private void printOperations(List<OperationDto> operations) {
        out.println(DataPresenter.operationListToString(operations));
    }

    // Methods that simplify common code ---------------------------------------------------------------------------- //

    private void displayStatus(Status status) {
        out.println(StatusMapper.statusToString(status));
    }

    private void displayStatus(Result<?> result) {
        out.println(StatusMapper.statusToString(result));
    }

    private boolean handleFailedResult(Result<?> result) {
        if (result.failure()) {
            displayStatus(result);
            return true;
        } else {
            return false;
        }
    }
}
