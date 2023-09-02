package ua.com.foxminded.schoolconsoleapp.utils.transactionutils.connectionconsumer;

import java.sql.Connection;

@FunctionalInterface
public interface ConnectionConsumer {
    void consume(Connection connection) throws Exception;
}
