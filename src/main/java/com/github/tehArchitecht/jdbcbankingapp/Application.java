package com.github.tehArchitecht.jdbcbankingapp;

import com.github.tehArchitecht.jdbcbankingapp.data.DbInitializer;
import com.github.tehArchitecht.jdbcbankingapp.presentation.BankTui;

public class Application {
    public static void main(String[] args) {
        DbInitializer.Initialize();
        new BankTui(System.in, System.out).run();
    }
}
