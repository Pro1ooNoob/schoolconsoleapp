package ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

public interface MyDataSource extends Closeable{
    Connection getConnection() throws SQLException;
}
