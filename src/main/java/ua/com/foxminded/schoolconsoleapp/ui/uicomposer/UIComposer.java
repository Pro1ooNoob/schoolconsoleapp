package ua.com.foxminded.schoolconsoleapp.ui.uicomposer;

public interface UIComposer {
    void setConsoleOutput(String ... consoleOutputStarting);
    void setUserArguments(String ... userArgument);
    void setErrorMessage(String errorMessage);
    void setMainSelectionWindow(String mainSelectionWindow);
    String getComposedInterface();
}
