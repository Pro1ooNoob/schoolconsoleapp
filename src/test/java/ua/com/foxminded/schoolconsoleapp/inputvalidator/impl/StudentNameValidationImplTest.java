package ua.com.foxminded.schoolconsoleapp.inputvalidator.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;

class StudentNameValidationImplTest {
    private InputValidator<String> nameValidator = new StudentNameValidationImpl();

    @ParameterizedTest
    @ValueSource(strings = "Ben Affleck")
    void validate_GivenValidName_ShouldReturnTrue(String fullName) throws IOException, ValidationException {
        fullName = fullName.trim();
        boolean result = nameValidator.validate(fullName);
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Ben ", " Affleck", "Ben Affleck Da", "23 Ben", "23", " ds dsf sd ", "dsfd dsf sdf" })
    void validate_GivenInvalidName_ShouldThrowException(String fullName1) {
        final String fullName = fullName1.trim();
        String expectedMessage = "Incorrect name: " + fullName;
        assertAll(() -> assertThrows(ValidationException.class, () -> {
            nameValidator.validate(fullName);
        }), () -> {
            Throwable throwable = assertThrows(ValidationException.class, () -> {
                nameValidator.validate(fullName);
            });
            assertEquals(expectedMessage, throwable.getMessage());
        });
    }
}
