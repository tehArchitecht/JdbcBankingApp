package com.github.tehArchitecht.jdbcbankingapp.logic.dto.request;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountRequest {
    private Currency currency;
}
