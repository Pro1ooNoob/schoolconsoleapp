package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;

public class GroupGeneratorImpl implements TestDataGenerator<List<String>> {
    private Random random = new Random();

    @Override
    public List<String> returnTestData() {
        List<String> groups = new ArrayList<String>();
        char char0, char1, char2, char3;
        for (int i = 0; i < 10; i++) {
            char0 = returnRandomAlphabeticCharacter();
            char1 = returnRandomAlphabeticCharacter();
            char2 = returnRandomNumericCharacter();
            char3 = returnRandomNumericCharacter();
            char[] arr = { char0, char1, '-', char2, char3 };
            groups.add(new String(arr));
        }
        return groups;
    }

    public Character returnRandomAlphabeticCharacter() {
        int rangeSelector = random.nextInt(2);
        if (rangeSelector == 0) {
            return (char) (65 + random.nextInt(26));
        } else {
            return (char) (97 + random.nextInt(26));
        }
    }

    public Character returnRandomNumericCharacter() {
        return (char) (48 + random.nextInt(10));
    }
}
