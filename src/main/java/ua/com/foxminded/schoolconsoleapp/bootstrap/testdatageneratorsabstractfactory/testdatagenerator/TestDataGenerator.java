package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator;

import java.io.IOException;

public interface TestDataGenerator<T> {
    T returnTestData() throws IOException;
}
