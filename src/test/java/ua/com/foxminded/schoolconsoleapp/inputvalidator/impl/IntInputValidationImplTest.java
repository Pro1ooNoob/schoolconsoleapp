package ua.com.foxminded.schoolconsoleapp.inputvalidator.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;

class IntInputValidationImplTest {
    private InputValidator<String> inputValidator = new IntInputValidationImpl();

    @Test
    void validate_WhenUserEnterIntValue_ShouldReturnTrue() throws IOException, ValidationException {
        String input = "24632";
        boolean result = inputValidator.validate(input);
        assertTrue(result, "When user inputs int value should be returned true");
    }

    @Test
    void validate_WhenUserEnterNotIntValue_ShouldThrowValidationException() throws IOException, ValidationException {
        String input = "jfslkdhjfhr";
        String expectedMessage = "Incorrect integer value: " + input;
        assertAll(() -> assertThrows(ValidationException.class, () -> {
            inputValidator.validate(input);
        }), () -> {
            Throwable throwable = assertThrows(ValidationException.class, () -> {
                inputValidator.validate(input);
            });
            assertEquals(expectedMessage, throwable.getMessage());
        });
    }
}
