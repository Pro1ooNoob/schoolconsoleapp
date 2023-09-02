package ua.com.foxminded.schoolconsoleapp.inputvalidator;

import java.io.IOException;

import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;

public interface InputValidator<T> {
    Boolean validate(T option) throws IOException, ValidationException;
}
