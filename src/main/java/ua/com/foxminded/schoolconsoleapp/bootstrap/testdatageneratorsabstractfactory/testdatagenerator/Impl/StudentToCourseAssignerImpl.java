package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;

public class StudentToCourseAssignerImpl implements TestDataGenerator<Map<Integer, ArrayList<Integer>>> {
    @Override
    public Map<Integer, ArrayList<Integer>> returnTestData() {
        Random random = new Random();
        Map<Integer, ArrayList<Integer>> studentCourseMap = new HashMap<>();

        for (int i = 0; i < 200; i++) {
            int coursesAmount = 1 + random.nextInt(3);
            ArrayList<Integer> coursesList = new ArrayList<>(coursesAmount);
            List<Integer> courseIds = Stream.iterate(1, k -> (k + 1)).limit(10)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (int j = 0; j < coursesAmount; j++) {
                int listIndex = random.nextInt(courseIds.size());
                int courseId = courseIds.remove(listIndex);
                coursesList.add(courseId);
            }
            int studentId = 1 + i;
            studentCourseMap.put(studentId, coursesList);
        }
        return studentCourseMap;
    }
}
