package ua.com.foxminded.schoolconsoleapp.ui.uicomposer.impl;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.UIViewReaderImpl;
import ua.com.foxminded.schoolconsoleapp.ui.uicomposer.UIComposer;

@TestInstance(Lifecycle.PER_CLASS)
class UIComposerImplTest {
    final private Path pathAskUserChoiceWindow = Paths.get("src", "test", "resources", "uicomposerimpltestresources",
            "txtresources", "consoleoutputstrings", "askuserchoicewindow.txt");
    final private Path pathShowChoiceWindow = Paths.get("src", "test", "resources", "uicomposerimpltestresources",
            "txtresources", "consoleoutputstrings", "showchoicewindow.txt");
    final private Path pathAskCourseName = Paths.get("src", "test", "resources", "uicomposerimpltestresources",
            "txtresources", "consoleoutputstrings", "askcoursename.txt");
    final private Path pathAskFullName = Paths.get("src", "test", "resources", "uicomposerimpltestresources",
            "txtresources", "consoleoutputstrings", "askfullname.txt");
    final private Path pathAskStudentId = Paths.get("src", "test", "resources", "uicomposerimpltestresources",
            "txtresources", "consoleoutputstrings", "askstudentid.txt");
    final private Path pathAskStudentAmount = Paths.get("src", "test", "resources", "uicomposerimpltestresources",
            "txtresources", "consoleoutputstrings", "askuseramount.txt");

    final private Path pathExpectedViewAError = Paths.get("src", "test", "resources", "uicomposerimpltestresources",
            "txtresources", "expectedviews", "expectedviewAerror.txt");
    final private Path pathExpectedViewAStudentsNumberError = Paths.get("src", "test", "resources",
            "uicomposerimpltestresources", "txtresources", "expectedviews", "expectedviewAstudentsnumbererror.txt");
    final private Path pathExpectedViewBCourseNameError = Paths.get("src", "test", "resources",
            "uicomposerimpltestresources", "txtresources", "expectedviews", "expectedviewBcoursenameerror.txt");
    final private Path pathExpectedViewCStudentNameError = Paths.get("src", "test", "resources",
            "uicomposerimpltestresources", "txtresources", "expectedviews", "expectedviewCstudentnameerror.txt");
    final private Path pathExpectedViewDStudentIdError = Paths.get("src", "test", "resources",
            "uicomposerimpltestresources", "txtresources", "expectedviews", "expectedviewDstudentiderror.txt");
    final private Path pathExpectedViewEStudentIdCourseNameError = Paths.get("src", "test", "resources",
            "uicomposerimpltestresources", "txtresources", "expectedviews",
            "expectedviewEstudentidcoursenameerror.txt");
    final private Path pathExpectedViewEStudentIdError = Paths.get("src", "test", "resources",
            "uicomposerimpltestresources", "txtresources", "expectedviews", "expectedviewEstudentiderror.txt");
    final private Path pathExpectedViewFStudentIdError = Paths.get("src", "test", "resources",
            "uicomposerimpltestresources", "txtresources", "expectedviews", "expectedviewFstudentiderror.txt");
    final private Path pathExpectedViewFStudentIdErrorCourseNameError = Paths.get("src", "test", "resources",
            "uicomposerimpltestresources", "txtresources", "expectedviews",
            "expectedviewFstudentidcoursenameerror.txt");

    private ResourceReader<String> askUserChoiceWindowReader = new UIViewReaderImpl(pathAskUserChoiceWindow);
    private ResourceReader<String> showChoiceWindowReader = new UIViewReaderImpl(pathShowChoiceWindow);
    private ResourceReader<String> askCourseNameReader = new UIViewReaderImpl(pathAskCourseName);
    private ResourceReader<String> askFullNameReader = new UIViewReaderImpl(pathAskFullName);
    private ResourceReader<String> askStudentIdReader = new UIViewReaderImpl(pathAskStudentId);
    private ResourceReader<String> askUserAmountReader = new UIViewReaderImpl(pathAskStudentAmount);

    private ResourceReader<String> expectedViewAErrorReader = new UIViewReaderImpl(pathExpectedViewAError);
    private ResourceReader<String> expectedViewAStudentsNumberErrorReader = new UIViewReaderImpl(
            pathExpectedViewAStudentsNumberError);
    private ResourceReader<String> expectedViewBCourseNameErrorReader = new UIViewReaderImpl(
            pathExpectedViewBCourseNameError);
    private ResourceReader<String> expectedViewCStudentNameErrorReader = new UIViewReaderImpl(
            pathExpectedViewCStudentNameError);
    private ResourceReader<String> expectedViewDStudentIdErrorReader = new UIViewReaderImpl(
            pathExpectedViewDStudentIdError);
    private ResourceReader<String> expectedViewEStudentIdCourseNameErrorReader = new UIViewReaderImpl(
            pathExpectedViewEStudentIdCourseNameError);
    private ResourceReader<String> expectedViewEStudentIdErrorReader = new UIViewReaderImpl(
            pathExpectedViewEStudentIdError);
    private ResourceReader<String> expectedViewFStudentIdErrorReader = new UIViewReaderImpl(
            pathExpectedViewFStudentIdError);
    private ResourceReader<String> expectedViewFStudentIdCourseNameErrorReader = new UIViewReaderImpl(
            pathExpectedViewFStudentIdErrorCourseNameError);

    private String expectedViewAErrorString;
    private String expectedViewAStudentsNumberErrorString;
    private String expectedViewBCourseNameErrorString;
    private String expectedViewCStudentNameErrorString;
    private String expectedViewDStudentIdErrorString;
    private String expectedViewEStudentIdCourseNameErrorString;
    private String expectedViewEStudentIdErrorString;
    private String expectedViewFStudentIdErrorString;
    private String expectedViewFStudentIdCourseNameErrorString;

    private String askUserChoiceWindowString;
    private String showChoiceWindowString;
    private String askCourseNameString;
    private String askFullNameString;
    private String askStudentIdString;
    private String askStudentAmountString;

    @BeforeEach
    void init() throws IOException {
        try {
            expectedViewAErrorString = expectedViewAErrorReader.read();
            expectedViewAStudentsNumberErrorString = expectedViewAStudentsNumberErrorReader.read();
            expectedViewBCourseNameErrorString = expectedViewBCourseNameErrorReader.read();
            expectedViewCStudentNameErrorString = expectedViewCStudentNameErrorReader.read();
            expectedViewDStudentIdErrorString = expectedViewDStudentIdErrorReader.read();
            expectedViewEStudentIdCourseNameErrorString = expectedViewEStudentIdCourseNameErrorReader.read();
            expectedViewEStudentIdErrorString = expectedViewEStudentIdErrorReader.read();
            expectedViewFStudentIdErrorString = expectedViewFStudentIdErrorReader.read();
            expectedViewFStudentIdCourseNameErrorString = expectedViewFStudentIdCourseNameErrorReader.read();

            askUserChoiceWindowString = askUserChoiceWindowReader.read();
            showChoiceWindowString = showChoiceWindowReader.read();
            askCourseNameString = askCourseNameReader.read();
            askFullNameString = askFullNameReader.read();
            askStudentIdString = askStudentIdReader.read();
            askStudentAmountString = askUserAmountReader.read();
        } catch (IOException e) {
            String message = "Can't read from file" + e;
            throw new IOException(message);
        }
    }

    @Test
    void getComposedInterface_GivenSelectionOptionError_ShouldReturnViewAError() {
        UIComposer uiComposer = new UIComposerImpl();
        final String errorMessage = "Incorrect option selected: Q1@";
        final String choiceWindowString = showChoiceWindowString;
        final String consoleOutput = askUserChoiceWindowString;

        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewAErrorStringList = Stream.of(expectedViewAErrorString.split("\n")).toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewAErrorStringList, actualViewList);
    }

    @Test
    void getComposedInterface_GivenSelectionOptoinAndStudentNumberError_ViewAStudentsNumberError() {
        UIComposer uiComposer = new UIComposerImpl();
        final String optionSelection = "A";
        final String errorMessage = "Incorrect integer value: 53kl";
        final String choiceWindowString = showChoiceWindowString;
        final String[] consoleOutput = { askUserChoiceWindowString, askStudentAmountString };

        uiComposer.setUserArguments(optionSelection);
        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewAStudentsNumberErrorStringList = Stream
                .of(expectedViewAStudentsNumberErrorString.split("\n")).toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewAStudentsNumberErrorStringList, actualViewList);
    }

    @Test
    void getComposedInterface_GivenSelectionOptionBAndCourseNameError_ViewBCourseNameErrorString() {
        UIComposer uiComposer = new UIComposerImpl();
        final String optionSelection = "B";
        final String errorMessage = "Can't find course name: invalid course name";
        final String choiceWindowString = showChoiceWindowString;
        final String[] consoleOutput = { askUserChoiceWindowString, askCourseNameString };

        uiComposer.setUserArguments(optionSelection);
        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewBCourseNameErrorStringList = Stream.of(expectedViewBCourseNameErrorString.split("\n"))
                .toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewBCourseNameErrorStringList, actualViewList);
    }

    @Test
    void getComposedInterface_GivenSelectionOptionCAndStudentNameError_ViewCStudentNameError() {
        UIComposer uiComposer = new UIComposerImpl();
        final String optionSelection = "C";
        final String errorMessage = "Incorrect name: Ben";
        final String choiceWindowString = showChoiceWindowString;
        final String[] consoleOutput = { askUserChoiceWindowString, askFullNameString };

        uiComposer.setUserArguments(optionSelection);
        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewCStudentNameErrorStringList = Stream
                .of(expectedViewCStudentNameErrorString.split("\n")).toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewCStudentNameErrorStringList, actualViewList);
    }

    @Test
    void getComposedInterface_GivenSelectionOptionDAndStudentIdError_ExpectedviewDstudentiderror() {
        UIComposer uiComposer = new UIComposerImpl();
        final String optionSelection = "D";
        final String errorMessage = "Incorrect integer value: 53kl";
        final String choiceWindowString = showChoiceWindowString;
        final String[] consoleOutput = { askUserChoiceWindowString, askStudentIdString };

        uiComposer.setUserArguments(optionSelection);
        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewDStudentIdErrorStringList = Stream.of(expectedViewDStudentIdErrorString.split("\n"))
                .toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewDStudentIdErrorStringList, actualViewList);
    }

    @Test
    void getComposedInterface_GivenSelectionOptionEStudentIdCourseNameError_ViewEStudentIdCourseNameError() {
        UIComposer uiComposer = new UIComposerImpl();
        final String[] optionSelection = { "E", "1" };
        final String errorMessage = "Can't find course name: invalid course name";
        final String choiceWindowString = showChoiceWindowString;
        final String[] consoleOutput = { askUserChoiceWindowString, askStudentIdString, askCourseNameString };

        uiComposer.setUserArguments(optionSelection);
        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewEStudentIdCourseNameErrorStringList = Stream
                .of(expectedViewEStudentIdCourseNameErrorString.split("\n")).toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewEStudentIdCourseNameErrorStringList, actualViewList);
    }

    @Test
    void getComposedInterface_GivenSelectionOptionEStudentIdError_ExpectedViewEstudentIdError() {
        UIComposer uiComposer = new UIComposerImpl();
        final String[] optionSelection = { "E" };
        final String errorMessage = "Incorrect integer value: 53kl";
        final String choiceWindowString = showChoiceWindowString;
        final String[] consoleOutput = { askUserChoiceWindowString, askStudentIdString };

        uiComposer.setUserArguments(optionSelection);
        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewEStudentIdErrorStringList = Stream.of(expectedViewEStudentIdErrorString.split("\n"))
                .toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewEStudentIdErrorStringList, actualViewList);
    }

    @Test
    void getComposedInterface_GivenSelectionOptionFStudentIdError_ExpectedViewFStudentIdError() {
        UIComposer uiComposer = new UIComposerImpl();
        final String[] optionSelection = { "F" };
        final String errorMessage = "Incorrect integer value: 53kl";
        final String choiceWindowString = showChoiceWindowString;
        final String[] consoleOutput = { askUserChoiceWindowString, askStudentIdString };

        uiComposer.setUserArguments(optionSelection);
        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewFStudentIdErrorStringList = Stream.of(expectedViewFStudentIdErrorString.split("\n"))
                .toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewFStudentIdErrorStringList, actualViewList);
    }

    @Test
    void getComposedInterface_GivenSelectionOptionCStudentIdCourseNameError_ExpectedViewFStudentIdCourseNameError() {
        UIComposer uiComposer = new UIComposerImpl();
        final String[] optionSelection = { "F", "1" };
        final String errorMessage = "Can't find course name: invalid course name";
        final String choiceWindowString = showChoiceWindowString;
        final String[] consoleOutput = { askUserChoiceWindowString, askStudentIdString, askCourseNameString };

        uiComposer.setUserArguments(optionSelection);
        uiComposer.setMainSelectionWindow(choiceWindowString);
        uiComposer.setErrorMessage(errorMessage);
        uiComposer.setConsoleOutput(consoleOutput);

        String actualView = uiComposer.getComposedInterface();

        List<String> expectedViewFStudentIdCourseNameErrorStringList = Stream
                .of(expectedViewFStudentIdCourseNameErrorString.split("\n")).toList();
        List<String> actualViewList = Stream.of(actualView.split("\n")).toList();

        assertLinesMatch(expectedViewFStudentIdCourseNameErrorStringList, actualViewList);
    }
}
