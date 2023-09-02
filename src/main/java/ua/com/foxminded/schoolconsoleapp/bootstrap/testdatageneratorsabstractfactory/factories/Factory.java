package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.factories;

import java.util.Optional;

import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;

public interface Factory<T> {
    Optional<TestDataGenerator<T>> getTestDataGenerator(String factoryType);
}
