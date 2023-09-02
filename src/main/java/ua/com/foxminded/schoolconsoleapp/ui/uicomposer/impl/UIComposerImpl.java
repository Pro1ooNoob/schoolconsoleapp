package ua.com.foxminded.schoolconsoleapp.ui.uicomposer.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ua.com.foxminded.schoolconsoleapp.ui.errorviewui.ErrorViewUI;
import ua.com.foxminded.schoolconsoleapp.ui.errorviewui.impl.ErrorViewUIImpl;
import ua.com.foxminded.schoolconsoleapp.ui.uicomposer.UIComposer;

public class UIComposerImpl implements UIComposer {
    private List<String> consoleOutputStartings = new ArrayList<>();
    private List<String> userArguments = new ArrayList<>();
    private String errorMessage;
    private String mainSelectionWindow;

    private ErrorViewUI errorViewUI = new ErrorViewUIImpl();

    @Override
    public void setConsoleOutput(String... consoleOutputStarting) {
        consoleOutputStartings = Arrays.asList(consoleOutputStarting);
    }

    @Override
    public void setUserArguments(String... userArgument) {
        userArguments = Arrays.asList(userArgument);

    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;

    }

    @Override
    public String getComposedInterface() {
        StringBuilder finalView = new StringBuilder();
        if (errorMessage != null && !errorMessage.isBlank()) {
            String errorScope = errorViewUI.show(errorMessage);
            finalView.append(errorScope);
        }
        finalView.append(mainSelectionWindow);
        for (int i = 0; i < consoleOutputStartings.size(); i++) {
            int j = i;
            if (j < consoleOutputStartings.size() - 1) {
                finalView.append(consoleOutputStartings.get(i).trim() + " " + userArguments.get(j)).append('\n');
            } else {
                finalView.append(consoleOutputStartings.get(i).trim() + " ");
            }
        }
        return new String(finalView);
    }

    @Override
    public void setMainSelectionWindow(String mainSelectionWindow) {
        this.mainSelectionWindow = mainSelectionWindow;

    }
}
