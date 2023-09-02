package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.factories.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.factories.Factory;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl.StudentToCourseAssignerImpl;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl.StudentToGroupAssignerImpl;

public class AssignerFactoryImpl implements Factory<Map<Integer, ArrayList<Integer>>> {
    @Override
    public Optional<TestDataGenerator<Map<Integer, ArrayList<Integer>>>> getTestDataGenerator(String factoryType) {
        if (factoryType.equalsIgnoreCase("StudentToCourseAssignerImpl")) {
            return Optional.of(new StudentToCourseAssignerImpl());
        } else if (factoryType.equalsIgnoreCase("StudentToGroupAssignerImpl")) {
            return Optional.of(new StudentToGroupAssignerImpl());
        }
        return Optional.empty();
    }
}
