package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.RepeatedTest;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;

class StudentToGroupAssignerImplTest {
    private final static int LOWER_LIMIT_SIZE_OF_STUDENTS = 10;
    private final static int UPPER_LIMIT_SIZE_OF_STUDENTS = 30;

    @RepeatedTest(20)
    void returnsAmountStudentsByGroupsMethod_AskForGroupsStudentsAmountMap_ReturnsMap() {
        StudentToGroupAssignerImpl assigner = new StudentToGroupAssignerImpl();
        Map<Integer, Integer> map = assigner.returnsAmountStudentsByGroupsMethod();
        boolean eachGroupHasValidSize = map.entrySet().stream().allMatch(entry -> {
            int amountOfStudents = entry.getValue();
            return (amountOfStudents >= LOWER_LIMIT_SIZE_OF_STUDENTS
                    && amountOfStudents <= UPPER_LIMIT_SIZE_OF_STUDENTS) || amountOfStudents == 0;
        });

        assertAll(() -> assertTrue(eachGroupHasValidSize, "Each group should have a valid size "),
                () -> assertTrue(map.size() <= 10, "amount of groups should be (0;10]"));
    }

    @RepeatedTest(20)
    void returnsGroupStudentIdsMap_AskForGroupStudentIdsMap_ReturnsGroupStudentIdsMap() throws IOException {
        TestDataGenerator<Map<Integer, ArrayList<Integer>>> studentGroupMap = new StudentToGroupAssignerImpl();
        Map<Integer, ArrayList<Integer>> groupStudentIdsAmountMap = studentGroupMap.returnTestData();
        boolean eachGroupHasValidStudentAmount = groupStudentIdsAmountMap.entrySet().stream().allMatch(entry -> {
            int studentAmount = entry.getValue().size();
            return (studentAmount >= LOWER_LIMIT_SIZE_OF_STUDENTS && studentAmount <= UPPER_LIMIT_SIZE_OF_STUDENTS)
                    || studentAmount == 0;
        });
        int totalAmountStudentIdsInGroups = groupStudentIdsAmountMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()).mapToInt(value -> value).sum();
        int totalAmountUniqueStudentIdsInGroups = groupStudentIdsAmountMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()).mapToInt(value -> value).distinct().sum();
        boolean allStudentIdsUnique = totalAmountStudentIdsInGroups == totalAmountUniqueStudentIdsInGroups;

        assertAll(
                () -> assertTrue(eachGroupHasValidStudentAmount,
                        "Each group should contain from 10 inclusive to 30 inclusive amount of students"),
                () -> assertTrue(allStudentIdsUnique, "All student Ids  should be unique"));
    }
}
