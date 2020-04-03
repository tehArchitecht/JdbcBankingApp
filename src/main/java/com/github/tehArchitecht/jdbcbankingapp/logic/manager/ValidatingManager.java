package com.github.tehArchitecht.jdbcbankingapp.logic.manager;

import javax.validation.Validation;
import javax.validation.Validator;

public class ValidatingManager {
    private final Validator validator;

    protected ValidatingManager() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    protected <T> boolean hasConstraintViolations(T object) {
        return !validator.validate(object).isEmpty();
    }
}
