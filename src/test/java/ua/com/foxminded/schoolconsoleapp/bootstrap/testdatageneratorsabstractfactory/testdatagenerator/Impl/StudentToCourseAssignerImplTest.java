package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.Impl;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StudentToCourseAssignerImplTest {
    @Test
    void returnTestDataShouldReturnMapStudentToCourses() {
        StudentToCourseAssignerImpl assigner = new StudentToCourseAssignerImpl();
        Map<Integer, ArrayList<Integer>> map = assigner.returnTestData();

        for (int i = 0; i < 200; i++) {
            final int studentId = i + 1;
            boolean eachCourseIsValid = map.get(studentId).stream()
                    .allMatch(courseId -> courseId >= 1 && courseId <= 10);
            int coursesAmount = map.get(studentId).size();
            boolean coursesAmountIsValid = coursesAmount >= 1 && coursesAmount <= 3;
            long amountCourses = map.get(studentId).stream().count();
            long amountUniqueCourses = map.get(studentId).stream().distinct().count();

            assertAll(
                    () -> assertTrue(coursesAmountIsValid, "Courses amount should be from 1 inclusive to 3 inclusive"),
                    () -> assertTrue(eachCourseIsValid,
                            "Each courseId should be in range from 1 inclusive to 10 inclusive."),
                    () -> assertEquals(amountCourses, amountUniqueCourses, "All courses should be unique"));
        }
    }
}
