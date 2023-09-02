package ua.com.foxminded.schoolconsoleapp.resourcereaders.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

class SimpleTxtLinesReaderImplTest {
    private Path path = Paths.get("src","test","resources","initialdata","coursenames.txt");
    
    @Test
    void testReadMethodShouldReturn10Courses() throws FileNotFoundException, IOException {
        ResourceReader<List<String>> txtReader = new SimpleTxtLinesReaderImpl(path);
        List<String> courses = txtReader.read();
        assertAll(
                () -> assertEquals(10, courses.size(), "The amount of courses should be 10"),
                () -> assertEquals("Quantum Mechanics and Applications", courses.get(0), "The zero course should be as expected"),
                () -> assertEquals("Sustainable Architecture and Urban Planning", courses.get(courses.size() - 1), "The last course should be as expected")
                );       
    }
}
