package ua.com.foxminded.schoolconsoleapp.ui.selectoptionui.impl;

import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.ui.selectoptionui.SelectOptionUI;

public class SelectOptionUIImpl implements SelectOptionUI{
    private ResourceReader<String> userChoiceViewReader;
    private ResourceReader<String> askUserToSelectionReader;
    private Logger log = LoggerFactory.getLogger(getClass());
    
    public SelectOptionUIImpl(ResourceReader<String> pathShowChoiceWindow, ResourceReader<String> pathUserChoiceView) {
        this.userChoiceViewReader = pathShowChoiceWindow;
        this.askUserToSelectionReader = pathUserChoiceView;
    }

    @Override
    public Optional<String> showChoiceWindow() throws IOException {
        String view = null;
        try {
            view = userChoiceViewReader.read();
        } catch (IOException e) {
            String message = "Error while reading choice window view: " + e;
            log.error(message);
            throw new IOException(message);
        }
        return Optional.of(view);
    }

    @Override
    public Optional<String> askUserChoice() throws IOException {
        String view = null;
        try {
            view = askUserToSelectionReader.read();
        } catch (IOException e) {
            String message = "Error while reading choice window view: " + e;
            log.error(message);
            throw new IOException(message);
        }
        return Optional.of(view);
    }
}
