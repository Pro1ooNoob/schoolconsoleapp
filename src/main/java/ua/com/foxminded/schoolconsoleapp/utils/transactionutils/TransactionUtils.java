package ua.com.foxminded.schoolconsoleapp.utils.transactionutils;

import java.sql.Connection;
import java.sql.SQLException;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;

public class TransactionUtils {
    public static <T> T mapTransaction(MyDataSource dataSource, ConnectionMapper<T> mapper) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                T result = mapper.apply(connection);
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw new SQLException("Exception in transaction", e);
            }
        }
    }

    public interface ConnectionMapper<T> {
        T apply(Connection connection) throws Exception;
    }
}
