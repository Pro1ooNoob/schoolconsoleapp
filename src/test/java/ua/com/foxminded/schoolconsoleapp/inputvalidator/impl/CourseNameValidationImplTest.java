package ua.com.foxminded.schoolconsoleapp.inputvalidator.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SimpleTxtLinesReaderImpl;

@TestInstance(Lifecycle.PER_CLASS)
class CourseNameValidationImplTest {
    private final Path pathCourseNames = Paths.get("src","test","resources","initialdata","coursenames.txt");
    private ResourceReader<List<String>> courseNamesReader = new SimpleTxtLinesReaderImpl(pathCourseNames);
    private InputValidator<String> courseNameValidator = new CourseNameValidationImpl(courseNamesReader);

    @ParameterizedTest
    @ArgumentsSource(validCoursesProvider.class)
    void validate_GivenRealCourse_ShouldReturnTrue(String courseName) throws IOException, ValidationException {
        boolean result = courseNameValidator.validate(courseName);
        assertTrue(result, "The result should be true if course name is valid");
    }
    
    @Test
    void validate_GivenNotRealCourse_ShouldThrowException () throws IOException, ValidationException {
        String courseName = "It is a wrong name for course";
        String expectedMessage = "Can't find course name: " + courseName;
        assertAll(
                () -> assertThrows(ValidationException.class, () -> {
                    courseNameValidator.validate(courseName);
                }),
                () -> {Throwable throwable = assertThrows(ValidationException.class, () -> {
                    courseNameValidator.validate(courseName);
                });
                assertEquals(expectedMessage, throwable.getMessage());
                }
        );
    }
}

class validCoursesProvider implements ArgumentsProvider {
    private final Path pathCourseNames = Paths.get("src","test","resources","initialdata","coursenames.txt");
    private ResourceReader<List<String>> courseNamesReader = new SimpleTxtLinesReaderImpl(pathCourseNames);
    private List<String> courseNames; 
    
    private validCoursesProvider() throws FileNotFoundException, IOException {
        this.courseNames = courseNamesReader.read(); 
    }
    
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
        return courseNames.stream() 
                .map(courseName -> Arguments.of(courseName));
    } 
}