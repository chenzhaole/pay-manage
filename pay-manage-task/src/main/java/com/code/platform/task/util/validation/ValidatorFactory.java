package com.code.platform.task.util.validation;

import javax.validation.Validation;
import javax.validation.Validator;

public class ValidatorFactory {
    private static Validator validator = null;

    public static Validator getInstance() {
        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
        return validator;
    }
}
