package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;

public class StudentToGroupAssignerImpl implements TestDataGenerator<Map<Integer, ArrayList<Integer>>> {
    private static final int LOWER_LIMIT_SIZE_OF_STUDENTS = 10;
    private static final int TOTAL_STUDENTS_AMOUNT = 200;
    private Map<Integer, ArrayList<Integer>> studentGroupMap = new HashMap<>();
    private Random random = new Random();

    @Override
    public Map<Integer, ArrayList<Integer>> returnTestData() {
        return returnsGroupStudentIdsMap();
    }

    public Map<Integer, Integer> returnsAmountStudentsByGroupsMethod() {
        int groupId = 0;
        int totalStudentsAmount = 0;
        int groupSize = 0;
        Map<Integer, Integer> groupStudentAmountMap = new HashMap<>();
        while ((groupSize = LOWER_LIMIT_SIZE_OF_STUDENTS + random.nextInt(21)) + totalStudentsAmount <= 200
                && groupStudentAmountMap.size() < 10) {
            groupId++;
            groupStudentAmountMap.put(groupId, groupSize);
            totalStudentsAmount += groupSize;
        }
        return groupStudentAmountMap;
    }

    public Map<Integer, ArrayList<Integer>> returnsGroupStudentIdsMap() {
        List<Integer> studentIds = Stream.iterate(1, n -> n + 1).limit(TOTAL_STUDENTS_AMOUNT)
                .collect(Collectors.toCollection(ArrayList::new));
        Map<Integer, Integer> groupStudentAmountMap = returnsAmountStudentsByGroupsMethod();

        for (int groupId : groupStudentAmountMap.keySet()) {
            ArrayList<Integer> studentIdsInGroup = new ArrayList<>();
            int studentIdsAmountInGroup = groupStudentAmountMap.getOrDefault(groupId, 0);
            for (int j = 0; j < studentIdsAmountInGroup; j++) {
                int index = random.nextInt(studentIds.size());
                studentIdsInGroup.add(studentIds.remove(index));
            }
            studentGroupMap.put(groupId, studentIdsInGroup);
        }
        return studentGroupMap;
    }
}
