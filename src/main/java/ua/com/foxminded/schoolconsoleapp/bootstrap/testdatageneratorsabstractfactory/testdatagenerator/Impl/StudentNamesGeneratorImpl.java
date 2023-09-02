package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

public class StudentNamesGeneratorImpl implements TestDataGenerator<List<String>> {
    ResourceReader<List<String>> firstNamesReader;
    ResourceReader<List<String>> lastNamesReader;
      
    public StudentNamesGeneratorImpl(ResourceReader<List<String>> firstNamesReader, ResourceReader<List<String>> lastNamesReader) {
        this.firstNamesReader = firstNamesReader;
        this.lastNamesReader = lastNamesReader;
    }

    @Override
    public List<String> returnTestData() throws IOException { 
        List<String> firstNames = firstNamesReader.read();
        List<String> lastNames = lastNamesReader.read();    
        List<String> names = new ArrayList<>(); 
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            int firstNameIndex = random.nextInt(20);
            int lastNameIndex = random.nextInt(20);
            String name = firstNames.get(firstNameIndex) + " " + lastNames.get(lastNameIndex);
            names.add(name);
        }     
        return names;
    }
}
