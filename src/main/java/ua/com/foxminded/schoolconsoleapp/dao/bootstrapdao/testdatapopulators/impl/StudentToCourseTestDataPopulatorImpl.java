package ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.TestDataPopulator;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;

public class StudentToCourseTestDataPopulatorImpl implements TestDataPopulator<Map<Integer, ArrayList<Integer>>> {
    private MyDataSource myDataSource;
    private static final String INSERT_ALL_IN_STUDENTS_TO_COURSES_TABLE_QUERY_READER = """ 
            INSERT INTO school.student_to_course (student_id, course_id) VALUES (?,?);
            """;  
    private Logger log = LoggerFactory.getLogger(getClass());
    public StudentToCourseTestDataPopulatorImpl(MyDataSource dbHandler) {
        this.myDataSource = dbHandler;
    }

    @Override
    public boolean populate(Map<Integer, ArrayList<Integer>> testDataList) throws SQLException, IOException {
        final String insertAllInStudentsToCoursesTableQuery = INSERT_ALL_IN_STUDENTS_TO_COURSES_TABLE_QUERY_READER;

        try (Connection connection = myDataSource.getConnection();
                PreparedStatement preparedStatement = connection
                        .prepareStatement(insertAllInStudentsToCoursesTableQuery)) {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : testDataList.entrySet()) {
                for (Integer courseId : entry.getValue()) {
                    Integer studentId = entry.getKey();
                    preparedStatement.setInt(1, studentId);
                    preparedStatement.setInt(2, courseId);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            String message = String.format(
                    "The error occured during insertion into StudentToCourse. SQLState %s. Error code %s",
                    e.getSQLState(), e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
        return true;
    }
}
