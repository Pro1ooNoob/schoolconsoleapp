package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import java.io.IOException;
import java.util.List;

import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

public class CourseGeneratorImpl implements TestDataGenerator<List<String>> {
    private ResourceReader<List<String>> reader;

    public CourseGeneratorImpl(ResourceReader<List<String>> reader) {
        this.reader = reader;
    }

    @Override
    public List<String> returnTestData() throws IOException {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new IOException(e.getMessage() + e);
        }
    }
}
