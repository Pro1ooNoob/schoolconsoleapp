package ua.com.foxminded.schoolconsoleapp.inputvalidator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

public class CourseNameValidationImpl implements InputValidator<String> {
    private ResourceReader<List<String>> courseNamesReader;
    private Logger log = LoggerFactory.getLogger(getClass());

    public CourseNameValidationImpl(ResourceReader<List<String>> courseNamesReader) {
        this.courseNamesReader = courseNamesReader;
    }

    @Override
    public Boolean validate(String option) throws IOException, ValidationException {
        List<String> courseNamesList = new ArrayList<>();
        try {
            courseNamesList = courseNamesReader.read();
        } catch (IOException e) {
            String message = "The error occurred during reading courses names. " + e;
            log.error(message);
            throw new IOException(message);
        }
        if (!courseNamesList.contains(option)) {
            throw new ValidationException("Can't find course name: " + option);
        }
        return true;
    }
}
