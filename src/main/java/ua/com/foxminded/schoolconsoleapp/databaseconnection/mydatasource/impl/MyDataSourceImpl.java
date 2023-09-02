package ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;

public class MyDataSourceImpl implements MyDataSource, Closeable {
    private final String url;
    private final String username;
    private final String password;
    private HikariConfig config = new HikariConfig();
    private HikariDataSource pool;

    public MyDataSourceImpl(Properties dbProperties) {
        this.url = dbProperties.getProperty("db.url");
        this.username = dbProperties.getProperty("db.username");
        this.password = dbProperties.getProperty("db.password");
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        pool = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    @Override
    public void close() throws IOException {
        pool.close();     
    } 
}
