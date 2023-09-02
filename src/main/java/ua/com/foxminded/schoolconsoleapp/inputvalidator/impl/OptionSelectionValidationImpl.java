package ua.com.foxminded.schoolconsoleapp.inputvalidator.impl;

import ua.com.foxminded.schoolconsoleapp.enumerations.optionselection.OptionSelection;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;

public class OptionSelectionValidationImpl implements InputValidator<String> {

    @Override
    public Boolean validate(String option) throws ValidationException {
        try {
            OptionSelection.valueOf(option.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Incorrect option selected: " + option);
        }
        return true;
    }
}
