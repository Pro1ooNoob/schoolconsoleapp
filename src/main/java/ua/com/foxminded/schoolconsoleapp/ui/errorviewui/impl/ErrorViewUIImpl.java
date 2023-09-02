package ua.com.foxminded.schoolconsoleapp.ui.errorviewui.impl;

import ua.com.foxminded.schoolconsoleapp.ui.errorviewui.ErrorViewUI;

public class ErrorViewUIImpl implements ErrorViewUI {

    @Override
    public String show(String errorMessage) {
        if (errorMessage == null) {
            throw new IllegalArgumentException("The input for ErrorViewUIImpl#show cannot be null");
        } else if (errorMessage.isBlank()) {
            throw new IllegalArgumentException("The input for ErrorViewUIImpl#show cannot be blank string");
        }
        StringBuilder formatView = new StringBuilder();
        errorMessage = errorMessage.trim();
        formatView.append("+");
        for (int i = 0; i < errorMessage.length() + 2; i++) {
            formatView.append("-");
        }
        formatView.append("+").append('\n');
        formatView.append("|");
        for (int i = 0; i < errorMessage.length() + 2; i++) {
            formatView.append(" ");
        }
        formatView.append("|").append('\n');
        formatView.append("|").append(" ");
        formatView.append(errorMessage);
        formatView.append(" ").append("|").append('\n');
        formatView.append("|");
        for (int i = 0; i < errorMessage.length() + 2; i++) {
            formatView.append(" ");
        }
        formatView.append("|").append('\n');
        formatView.append("+");
        for (int i = 0; i < errorMessage.length() + 2; i++) {
            formatView.append("-");
        }
        formatView.append("+").append('\n');
        return new String(formatView);
    }
}
