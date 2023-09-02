package ua.com.foxminded.schoolconsoleapp.inputvalidator.impl;

import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;

public class StudentNameValidationImpl implements InputValidator<String> {
    @Override
    public Boolean validate(String option) throws ValidationException {
        String name = option.trim();
        String regex = "^[a-zA-Z]+ [a-zA-Z]+$";
        if (!name.matches(regex)) {
            throw new ValidationException("Incorrect name: " + name);
        }
        return true;
    }
}
