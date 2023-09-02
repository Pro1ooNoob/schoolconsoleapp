package ua.com.foxminded.schoolconsoleapp.ui.errorviewui.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.UIViewReaderImpl;
import ua.com.foxminded.schoolconsoleapp.ui.errorviewui.ErrorViewUI;

class ErrorViewUIImplTest {
    private ErrorViewUI errorViewUI = new ErrorViewUIImpl();

    @ParameterizedTest
    @ArgumentsSource(FormatProvider.class)
    void show_GivenString_ShouldReturnAppropriateExpectedFormat(String formatView, String inputForFormat) {
        String actualFormat = errorViewUI.show(inputForFormat);
        assertEquals(formatView, actualFormat);
    }

    @Test
    void show_GivenBlankString_ShouldThrowException() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> {
            errorViewUI.show("");
        }), () -> {
            String expectedMessage = "The input for ErrorViewUIImpl#show cannot be blank string";
            Throwable throwable = assertThrows(IllegalArgumentException.class, () -> {
                errorViewUI.show("");
            });
            assertEquals(expectedMessage, throwable.getMessage());
        });
    }

    @Test
    void show_GivenNull_ShouldThrowException() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> {
            errorViewUI.show(null);
        }), () -> {
            String expectedMessage = "The input for ErrorViewUIImpl#show cannot be null";
            Throwable throwable = assertThrows(IllegalArgumentException.class, () -> {
                errorViewUI.show(null);
            });
            assertEquals(expectedMessage, throwable.getMessage());
        });
    }
}

class FormatProvider implements ArgumentsProvider {
    final private Path pathExpectedFormat0 = Paths.get("src", "test", "resources", "errorviewuiimpltestresources",
            "txtresources", "expectedformats", "expectedformat0.txt");
    final private Path pathExpectedFormat1 = Paths.get("src", "test", "resources", "errorviewuiimpltestresources",
            "txtresources", "expectedformats", "expectedformat1.txt");
    final private Path pathExpectedFormat2 = Paths.get("src", "test", "resources", "errorviewuiimpltestresources",
            "txtresources", "expectedformats", "expectedformat2.txt");
    final private Path pathInputForFormat0 = Paths.get("src", "test", "resources", "errorviewuiimpltestresources",
            "txtresources", "inputforformat", "inputforformat0.txt");
    final private Path pathInputForFormat1 = Paths.get("src", "test", "resources", "errorviewuiimpltestresources",
            "txtresources", "inputforformat", "inputforformat1.txt");
    final private Path pathInputForFormat2 = Paths.get("src", "test", "resources", "errorviewuiimpltestresources",
            "txtresources", "inputforformat", "inputforformat2.txt");

    final private ResourceReader<String> expectedFormat0Reader = new UIViewReaderImpl(pathExpectedFormat0);
    final private ResourceReader<String> expectedFormat1Reader = new UIViewReaderImpl(pathExpectedFormat1);
    final private ResourceReader<String> expectedFormat2Reader = new UIViewReaderImpl(pathExpectedFormat2);

    final private ResourceReader<String> inputForFormat0Reader = new UIViewReaderImpl(pathInputForFormat0);
    final private ResourceReader<String> inputForFormat1Reader = new UIViewReaderImpl(pathInputForFormat1);
    final private ResourceReader<String> inputForFormat2Reader = new UIViewReaderImpl(pathInputForFormat2);

    final private Map<ResourceReader<String>, ResourceReader<String>> map = new HashMap<>() {
        /**
         * 
         */
        private static final long serialVersionUID = 8942446610588261567L;

        {
            put(expectedFormat0Reader, inputForFormat0Reader);
            put(expectedFormat1Reader, inputForFormat1Reader);
            put(expectedFormat2Reader, inputForFormat2Reader);
        }
    };

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
        return map.entrySet().stream().map(entry -> {
            String expectedFormat = null;
            String inputForFormat = null;
            try {
                expectedFormat = entry.getKey().read();
                inputForFormat = entry.getValue().read();
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return Arguments.of(expectedFormat, inputForFormat);
        });
    }
}
