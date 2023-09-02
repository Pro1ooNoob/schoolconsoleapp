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

public class GroupsTestDataPopulatorImpl implements TestDataPopulator<List<String>> {
    private MyDataSource myDataSource;
    private static final String INSERT_GROUP_NAME_QUERY = """ 
            INSERT INTO school.groups (group_name) VALUES (?);
            """;
    private Logger log = LoggerFactory.getLogger(getClass());

    public GroupsTestDataPopulatorImpl(MyDataSource dbHandler) {
        this.myDataSource = dbHandler;
    }

    @Override
    public boolean populate(List<String> testDataList) throws SQLException, IOException {
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GROUP_NAME_QUERY)) {
            for (int i = 0; i < testDataList.size(); i++) {
                preparedStatement.setString(1, testDataList.get(i));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            String message = String.format(
                    "The error occured during insertion in groups table. SQLState %s, Error code %s", e.getSQLState(),
                    e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
        return true;
    }
}
