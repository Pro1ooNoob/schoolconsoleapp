package ua.com.foxminded.schoolconsoleapp.ui.outputui.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SimpleTxtLinesReaderImpl;
import ua.com.foxminded.schoolconsoleapp.ui.outputui.OutputUI;

class TestDataBuilder1 {
    static List<StudentsDTO> returnNotEmptyList() {
        return Arrays.asList(
                new StudentsDTO(1L, 10L, "Ethan", "Smith"),
                new StudentsDTO(2L, 11L, "Olivia", "Johnson"),
                new StudentsDTO(3L, 12L, "Liam", "Williams")
                );
    }
    static List<StudentsDTO> returnEmptyList() {
        return Arrays.asList();
    }
}

class OutputStudentsUIImplTest {
    private OutputUI<StudentsDTO> outputUI = new OutputStudentsUIImpl<>();
    private final Path pathExpectedFormatIfResultSetEmpty = Paths.get("src","test","resources","outputuistudentdtoimpltestresources","expectedformat","expectedformatifresultsetempty.txt");
    private final Path pathExpectedFormatIfResultSetNotEmpty = Paths.get("src","test","resources","outputuistudentdtoimpltestresources","expectedformat","expectedformatifresultsetnotempty.txt");
    
    @Test 
    void returnView_WhenPassNotEmptyList_ShouldBeReturnedTitleAndResultSet() throws FileNotFoundException, IOException {     
        ResourceReader<List<String>> reader = new SimpleTxtLinesReaderImpl(pathExpectedFormatIfResultSetNotEmpty);
        String expectedFormat = reader.read().stream() 
                .collect(Collectors.joining("\n"));     
        List<StudentsDTO> listDTO = TestDataBuilder1.returnNotEmptyList();
        String actualFormat = outputUI.returnView(listDTO);
        
        assertEquals(expectedFormat, actualFormat, "Expected and actual format should be equal");      
    }
    
    @Test 
    void returnView_WhenPassEmptyList_ShouldBeReturnedOnlyTitle() throws FileNotFoundException, IOException {
        ResourceReader<List<String>> reader = new SimpleTxtLinesReaderImpl(pathExpectedFormatIfResultSetEmpty);
        String expectedFormat = reader.read().stream() 
                .collect(Collectors.joining("\n"));       
        List<StudentsDTO> listDTO = TestDataBuilder1.returnEmptyList();
        String actualFormat = outputUI.returnView(listDTO);
        
        assertEquals(expectedFormat, actualFormat, "Expected and actual format should be equal");   
    }   
}
