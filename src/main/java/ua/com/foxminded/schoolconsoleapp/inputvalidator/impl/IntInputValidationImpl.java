package ua.com.foxminded.schoolconsoleapp.inputvalidator.impl;

import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;

public class IntInputValidationImpl implements InputValidator<String> {

    @Override
    public Boolean validate(String option) throws ValidationException {
        try {
            Integer.valueOf(option);
        } catch (Exception e) {
            throw new ValidationException("Incorrect integer value: " + option);
        }
        return true;
    }
}
