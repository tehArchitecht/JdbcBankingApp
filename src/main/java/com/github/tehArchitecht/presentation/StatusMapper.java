package com.github.tehArchitecht.presentation;

import com.github.tehArchitecht.logic.Result;
import com.github.tehArchitecht.logic.Status;

public class StatusMapper {
    public static String statusToString(Status status) {
        switch (status) {
            case IS_NAME_IN_USE_SUCCESS: return "";
            case SIGN_UP_SUCCESS: return "Регистрация прошла успешно.";
            case SIGN_IN_SUCCESS: return "Вход в систему прошёл успешно.";
            case SIGN_IN_FAILURE_WRONG_DATA: return "Ошибка. Неверный логин и/или пароль.";
            case GET_USER_OPERATIONS_SUCCESS: return "";
            case GET_USER_ACCOUNTS_SUCCESS: return "";
            case CREATE_ACCOUNT_SUCCESS: return "Аккаунт создан.";
            case DEPOSIT_FUNDS_SUCCESS: return "Пополнение счета прошло успешно.";
            case DEPOSIT_FUNDS_FAILURE_INVALID_ACCOUNT_ID: return "Ошибка. Такого аккаунта не существует.";
            case DEPOSIT_FUNDS_FAILURE_UNAUTHORIZED_ACCESS: return "Ошибка. Несанкционированный доступ.";
            case TRANSFER_FUNDS_SUCCESS: return "Перевод прошел успешно.";
            case TRANSFER_FUNDS_FAILURE_INVALID_ACCOUNT_ID: return "Ошибка. Такого аккаунта не существует.";
            case TRANSFER_FUNDS_FAILURE_INVALID_PHONE_NUMBER: return "Ошибка. Пользователя с таким номером телефона не существует.";
            case TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_ACCOUNTS: return "Ошибка. У получателя нет счетов в системе.";
            case TRANSFER_FUNDS_FAILURE_INSUFFICIENT_FUNDS: return "Ошибка. На счету недостаточно средств.";
            case TRANSFER_FUNDS_FAILURE_UNAUTHORIZED_ACCESS: return "Ошибка. Несанкционированный доступ.";
            case BAD_TOKEN: return "Ошибка. Недействительный токен. Попробуйте заново войти в систему.";
            // helper status [start]
            case SUCCESS: return null;
            case INVALID_ACCOUNT_ID: return null;
            case INTERNAL_ERROR: return null;
            case UNAUTHORIZED_ACCESS: return null;
            // helper status [end]
            default: return "Ошибка. Произошел сбой в системе.";
        }
    }

    public static String statusToString(Result result) {
        return  statusToString(result.getStatus());
    }
}
