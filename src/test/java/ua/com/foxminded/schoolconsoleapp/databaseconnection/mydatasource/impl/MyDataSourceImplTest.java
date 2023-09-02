package ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.PropertiesReaderImpl;

@TestInstance(Lifecycle.PER_CLASS)
class MyDataSourceImplTest {
    private final Path pathDatabaseAccessInfo = Paths.get("src", "test", "resources", "config",
            "databaseaccessinfo.properties");
    private Logger log = LoggerFactory.getLogger(getClass());
    private Properties dbProperties = new Properties();
    private ResourceReader<Properties> dbPropertiesReader = new PropertiesReaderImpl(pathDatabaseAccessInfo);
    private MyDataSource myDataSource;

    @BeforeAll
    void setUp() throws IOException {
        dbProperties = dbPropertiesReader.read();
        myDataSource = new MyDataSourceImpl(dbProperties);
    }

    @Test
    void getConnection_AskForConnection_ShouldReturnValidConnection() throws SQLException {
        try (Connection connection = myDataSource.getConnection()) {
            try {
                assertTrue(!connection.isClosed(), "The connection should be opened");
            } catch (SQLException e) {
                log.error("Error while checking connection status:", e);
                throw new SQLException("Error while checking connection status:", e);
            }
        }
    }
}
