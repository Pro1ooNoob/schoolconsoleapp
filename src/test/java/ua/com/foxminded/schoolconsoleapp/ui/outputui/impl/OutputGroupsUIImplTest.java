package ua.com.foxminded.schoolconsoleapp.ui.outputui.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SimpleTxtLinesReaderImpl;
import ua.com.foxminded.schoolconsoleapp.ui.outputui.OutputUI;

class TestDataBuilder {
    public static List<GroupsDTO> createListOfValues() {
        return Arrays.asList(
                new GroupsDTO(1L, "AA-11"), 
                new GroupsDTO(2L, "BB-22"),
                new GroupsDTO(3L, "CC-33")
                );
    }
    public static List<GroupsDTO> createEmptyList () {
        return Arrays.asList();
    }  
}

class OutputGroupsUIImplTest {  
    private final Path pathExpectedFormatIfResultSetEmpty = Paths.get("src","test","resources", "outputuigroupsresources", "expectedformat", "expectedformatifresultsetempty.txt");
    private final Path pathExpectedFormatIfResultSetNotEmpty = Paths.get("src", "test", "resources", "outputuigroupsresources", "expectedformat", "expectedformatifresultsetnotempty.txt");

    @Test
    void returnView_WhenPassEmptyList_ShouldReturnOnlyTitle() throws FileNotFoundException, IOException {
        List<GroupsDTO> listDTO = TestDataBuilder.createEmptyList(); 
        ResourceReader<List<String>> reader = new SimpleTxtLinesReaderImpl(pathExpectedFormatIfResultSetEmpty);
        String expectedFormat = reader.read().stream()
                .collect(Collectors.joining("\n"));     
        OutputUI<GroupsDTO> outputUI = new OutputGroupsUIImpl<>();   
        String actualFormat = outputUI.returnView(listDTO);
        
        assertEquals(expectedFormat,actualFormat,"Expected and actual format should be equal");    
    }
    
    @Test 
    void returnView_WhenPassNotEmptyList_ShouldReturnTitleAndResultSet() throws FileNotFoundException, IOException {
        List<GroupsDTO> listDTO = TestDataBuilder.createListOfValues();        
        ResourceReader<List<String>> reader = new SimpleTxtLinesReaderImpl(pathExpectedFormatIfResultSetNotEmpty);
        String expectedFormat = reader.read().stream()
                .collect(Collectors.joining("\n")); 
        OutputUI<GroupsDTO> outputUI = new OutputGroupsUIImpl<>();        
        String actualFormat = outputUI.returnView(listDTO);
        
        assertEquals(expectedFormat,actualFormat,"Expected and actual format should be equal");
    }
}
