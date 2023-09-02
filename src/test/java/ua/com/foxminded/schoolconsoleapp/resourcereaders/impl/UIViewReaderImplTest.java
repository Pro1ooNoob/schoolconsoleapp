package ua.com.foxminded.schoolconsoleapp.resourcereaders.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

class UIViewReaderImplTest {
    final Path pathShowChoiceWindow = Paths.get("src", "test", "resources", "selectoptionuiimpltestresources",
            "showchoicewindow", "showchoicewindow.txt");

    @Test
    void UIViewReaderImpl_AskToReturnFormatView_ShouldReturnCorrectFormat() throws FileNotFoundException, IOException {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(pathShowChoiceWindow.toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        }
        String UIViewExpected = new String(builder);
        ResourceReader<String> reader = new UIViewReaderImpl(pathShowChoiceWindow);
        final String UIViewActual = reader.read();

        assertEquals(UIViewExpected, UIViewActual);
    }
}
