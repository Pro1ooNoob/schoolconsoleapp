package ua.com.foxminded.schoolconsoleapp.ui.selectoptionui;

import java.io.IOException;
import java.util.Optional;

public interface SelectOptionUI {
    Optional<String> showChoiceWindow() throws IOException;
    Optional<String> askUserChoice() throws IOException;
}
