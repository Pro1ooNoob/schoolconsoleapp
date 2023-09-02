package ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.TestDataPopulator;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;


public class StudentsTestDataPopulatorImpl implements TestDataPopulator<List<String>> {
    private MyDataSource myDataSource;
    private static final String INSERT_STUDENT_NAMES_QUERY = """ 
            INSERT INTO school.students (first_name, last_name) VALUES (?,?);
            """;
    private Logger log = LoggerFactory.getLogger(getClass());

    public StudentsTestDataPopulatorImpl(MyDataSource dbHandler) {
        this.myDataSource = dbHandler;
    }

    @Override
    public boolean populate(List<String> testDataList) throws SQLException, IOException {

        try (Connection connection = myDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT_NAMES_QUERY)) {
            for (int i = 0; i < testDataList.size(); i++) {
                final String[] firstLastName = testDataList.get(i).split(" ");
                final String firstName = firstLastName[0];
                final String lastName = firstLastName[1];
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            String message = String.format(
                    "The error occurred during insertion in students table. SQLState %s, Error code %s",
                    e.getSQLState(), e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
        return true;
    }
}
