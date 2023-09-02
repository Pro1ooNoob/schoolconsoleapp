package ua.com.foxminded.schoolconsoleapp.inputvalidator.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;

class OptionSelectionValidationImplTest {
    private InputValidator<String> validator = new OptionSelectionValidationImpl();

    @Test
    void validate_SetCorrectSelection_ShouldBeReturnedTrue() throws IOException, ValidationException {
        Boolean result = validator.validate("A");
        assertTrue(result);
    }

    void validate_SetIncorrectSelections_ShouldThrowException() {
        String option = "H";
        String expectedMessage = "Incorrect option selected: " + option;
        assertAll(() -> assertThrows(ValidationException.class, () -> {
            validator.validate("H");
        }), () -> {
            Throwable throwable = assertThrows(ValidationException.class, () -> {

            });
            assertEquals(expectedMessage, throwable.getMessage());
        });
    }
}
