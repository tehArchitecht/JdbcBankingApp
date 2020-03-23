package com.github.tehArchitecht;

import com.github.tehArchitecht.data.DbInitializer;
import com.github.tehArchitecht.presentation.BankTui;

public class Application {
    public static void main(String[] args) {
        DbInitializer.Initialize();
        new BankTui(System.in, System.out).run();
    }
}
