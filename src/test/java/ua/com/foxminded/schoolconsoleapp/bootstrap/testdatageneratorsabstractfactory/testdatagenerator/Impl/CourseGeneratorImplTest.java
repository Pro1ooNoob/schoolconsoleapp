package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SimpleTxtLinesReaderImpl;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CourseGeneratorImplTest {
    ResourceReader<List<String>> reader = mock(SimpleTxtLinesReaderImpl.class);
    TestDataGenerator<List<String>> generator = new CourseGeneratorImpl(reader);

    @BeforeAll
    void setup() throws IOException {
        when(reader.read()).then((invocation) -> {
            Path path = Paths.get("src","test","resources","initialdata","coursenames.txt");
            List<String> courses = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    courses.add(line);
                }
            }
            return courses;
        });
    }

    @Test
    void testReturnTestDataMethodShouldReturn10Courses() throws IOException {
        List<String> courses = generator.returnTestData();
        assertAll(() -> assertEquals(10, courses.size(), "The amount of courses should be 10"),
                () -> assertEquals("Quantum Mechanics and Applications", courses.get(0),
                        "The zero course should be as expected"),
                () -> assertEquals("Sustainable Architecture and Urban Planning", courses.get(courses.size() - 1),
                        "The last course should be as expected"));
    }
}
