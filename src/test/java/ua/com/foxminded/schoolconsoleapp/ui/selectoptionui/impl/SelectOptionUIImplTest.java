package ua.com.foxminded.schoolconsoleapp.ui.selectoptionui.impl;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.UIViewReaderImpl;
import ua.com.foxminded.schoolconsoleapp.ui.selectoptionui.SelectOptionUI;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class SelectOptionUIImplTest {
    private final Path pathShowChoiceWindow = Paths.get("src", "test","resources","selectoptionuiimpltestresources", "showchoicewindow", "showchoicewindow.txt");
    private final Path pathAskUserToSelection = Paths.get("src","test","resources","selectoptionuiimpltestresources","askuserchoicewindow","askuserchoicewindow.txt");
    private final Path pathCompleteView = Paths.get("src","test","resources","selectoptionuiimpltestresources","completeview","completeview.txt"); 
   
    private ResourceReader<String> userChoiceViewReader = mock(UIViewReaderImpl.class);
    private ResourceReader<String> askUserToSelectionReader = mock(UIViewReaderImpl.class);
    private SelectOptionUI optionSelector = new SelectOptionUIImpl(userChoiceViewReader, askUserToSelectionReader);
    
    @BeforeAll
    void init() throws FileNotFoundException, IOException {
        ResourceReader<String> reader = new UIViewReaderImpl(pathShowChoiceWindow);
        String ChoiceWindowViewReader = reader.read();
        when(userChoiceViewReader.read()).thenReturn(ChoiceWindowViewReader);
        
        ResourceReader<String> reader1 = new UIViewReaderImpl(pathAskUserToSelection);
        String askUserChoiceWindowView = reader1.read();
        when(askUserToSelectionReader.read()).thenReturn(askUserChoiceWindowView);
    }

    @Test
    void UIViewReaderImpl_AskToReturnCompleteView_ShouldReturnCorrectFormat() throws FileNotFoundException, IOException {   
        ResourceReader<String> reader = new UIViewReaderImpl(pathCompleteView);
        String expectedCompleteView = reader.read().trim();  
        StringBuilder completeView = new StringBuilder();
        String view = optionSelector.showChoiceWindow().get();;
        completeView.append(view);
        completeView.append('\n');
        String view1 = optionSelector.askUserChoice().get();;
        completeView.append(view1);
        String actualView = new String(completeView);
        
        List<String> expectedCompleteViewList = Arrays.asList(expectedCompleteView.split("\n"));
        List<String> actualViewList = Arrays.asList(actualView.split("\n"));
        
        assertLinesMatch(expectedCompleteViewList, actualViewList);        
    }
}
