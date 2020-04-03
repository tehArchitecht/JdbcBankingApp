package com.github.tehArchitecht.jdbcbankingapp.logic.manager;

import org.apache.log4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidatingManager {
    private final static Logger logger = Logger.getLogger(ValidatingManager.class);

    private final Validator validator;

    protected ValidatingManager() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    protected <T> boolean hasConstraintViolations(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        for (ConstraintViolation<T> violation : violations)
            logger.warn(violation.getPropertyPath() + " " + violation.getMessage());
        return !violations.isEmpty();
    }
}
