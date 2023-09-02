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

public class StudentToGroupAllocatorImpl implements TestDataPopulator<Map<Integer, ArrayList<Integer>>> {

    private MyDataSource myDataSource;
    private static final String INSERT_GROUP_ID_TO_STUDENT_QUERY_READER = """ 
            UPDATE school.students 
            SET group_id = ? 
            WHERE group_id IS NULL AND student_id = ?;
            """;
    private Logger log = LoggerFactory.getLogger(getClass());

    public StudentToGroupAllocatorImpl(MyDataSource dbHandler) {
        this.myDataSource = dbHandler;
    }

    @Override
    public boolean populate(Map<Integer, ArrayList<Integer>> testDataList)
            throws SQLException, IOException {
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GROUP_ID_TO_STUDENT_QUERY_READER)) {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : testDataList.entrySet()) {
                for (Integer studentId : entry.getValue()) {
                    Integer groupId = entry.getKey();
                    preparedStatement.setInt(1, groupId);
                    preparedStatement.setInt(2, studentId);
                    preparedStatement.addBatch();
                }
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            String message = String.format(
                    "The error occured during insertion group ID to students. SQLState %s, Error code %s",
                    e.getSQLState(), e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
        return true;
    }
}
