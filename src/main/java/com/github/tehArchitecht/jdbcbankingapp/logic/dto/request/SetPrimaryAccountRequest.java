package com.github.tehArchitecht.jdbcbankingapp.logic.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SetPrimaryAccountRequest {
    @NotNull
    private UUID accountId;
}
