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
import org.junit.jupiter.api.BeforeEach;
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
class StudentNamesGeneratorImplTest {
    ResourceReader<List<String>> firstNames = mock(SimpleTxtLinesReaderImpl.class);
    ResourceReader<List<String>> lastNames = mock(SimpleTxtLinesReaderImpl.class);
    TestDataGenerator<List<String>> generator = new StudentNamesGeneratorImpl(firstNames, lastNames);

    @BeforeEach
    void setup() throws IOException {
        when(firstNames.read()).then(invocation -> {          
            Path path = Paths.get("src","test","resources","initialdata","studentsfirstnames.txt");
            List<String> firstNames = new ArrayList<>();
            
            try(BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    firstNames.add(line);
                }
            } 
            return firstNames;
        });
        
        when(lastNames.read()).then(invocation -> {     
            Path path = Paths.get("src","test","resources","initialdata","studentslastname.txt");
            List<String> lastNames = new ArrayList<>();
            
            try(BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lastNames.add(line);
                }
            }
            return lastNames;
        });
    }

    @Test
    void testReturnTestDataMethodShouldReturnStudentNames() throws IOException {
        List<String> names = generator.returnTestData();
        assertAll(() -> assertEquals(200, names.size(), "The List should be of 200 elements"), () -> {
            for (int i = 0; i < names.size(); i++) {
                assertNotNull(names.get(i), "The value should not be null");
                assertTrue(!names.get(i).isEmpty(), "The string should not be empty");
            }
        });
    }
}
