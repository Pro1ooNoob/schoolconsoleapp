package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;

@ExtendWith(MockitoExtension.class)
class GroupGeneratorImplTest {
    @Test
    void testReturnTestDataMethodShouldReturnAppropriateGroupName() throws IOException {
        TestDataGenerator<List<String>> generator = new GroupGeneratorImpl();
        List<String> groups = generator.returnTestData();
        String regex = "^([a-zA-Z]){2}-([0-9]{2})$";
        assertAll(() -> assertEquals(10, groups.size(), "The amount of groups should be 10"), () -> {
            for (int i = 0; i < groups.size(); i++) {
                assertTrue(groups.get(i).matches(regex));
            }
        });
    }

    @Test
    void returnRandomAlphabeticCharacterMethod() {
        GroupGeneratorImpl generator = new GroupGeneratorImpl();
        assertAll(
                () -> assertTrue(generator.returnRandomNumericCharacter() instanceof Character,
                        "The return type should be character"),
                () -> assertTrue(Character.isAlphabetic(generator.returnRandomAlphabeticCharacter()),
                        "The character should be numeric"));
    }

    @Test
    void returnRandomNumericCharacter() {
        GroupGeneratorImpl generator = new GroupGeneratorImpl();
        assertAll(
                () -> assertTrue(generator.returnRandomAlphabeticCharacter() instanceof Character,
                        "The return type should be character"),
                () -> assertTrue(Character.isDigit(generator.returnRandomNumericCharacter()),
                        "The character should be numeric"));
    }
}
