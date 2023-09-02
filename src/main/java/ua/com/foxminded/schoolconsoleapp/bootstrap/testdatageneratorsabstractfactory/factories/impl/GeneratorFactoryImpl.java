package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.factories.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.factories.Factory;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl.CourseGeneratorImpl;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl.GroupGeneratorImpl;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl.StudentNamesGeneratorImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SimpleTxtLinesReaderImpl;

public class GeneratorFactoryImpl implements Factory<List<String>> {
    @Override
    public Optional<TestDataGenerator<List<String>>> getTestDataGenerator(String factoryType) {
        if (factoryType.equalsIgnoreCase("CourseGeneratorImpl")) {
            Path pathToCourseNames = Paths.get("src", "main", "resources", "initialdata", "coursenames.txt");
            return Optional.of(new CourseGeneratorImpl(new SimpleTxtLinesReaderImpl(pathToCourseNames)));
        } else if (factoryType.equalsIgnoreCase("GroupGeneratorImpl")) {
            return Optional.of(new GroupGeneratorImpl());
        } else if (factoryType.equalsIgnoreCase("StudentNamesGeneratorImpl")) {
            Path pathToFirstNames = Paths.get("src", "main", "resources", "initialdata", "studentsfirstnames.txt");
            Path pathToLastNames = Paths.get("src", "main", "resources", "initialdata", "studentslastname.txt");
            return Optional.of(new StudentNamesGeneratorImpl(new SimpleTxtLinesReaderImpl(pathToFirstNames),
                    new SimpleTxtLinesReaderImpl(pathToLastNames)));
        }
        return Optional.empty();
    }
}
