package ua.com.foxminded.schoolconsoleapp.testservises.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;

public class TableConfiguratoImpl implements TableConfigurator {
    public TableConfiguratoImpl(Connection connection) {
        this.connection = connection;
    }
    private static final Path PATH_CREATE_SCHEMA = Paths.get("src", "test", "resources",
            "bootstrapdatabaseconfigure", "createschema.sql");
    private static final Path PATH_DROP_SCHEMA = Paths.get("src", "test", "resources",
            "bootstrapdatabaseconfigure", "dropschema.sql");
    private static final Path PATH_CREATE_ALL_TABLES = Paths.get("src", "test", "resources",
            "bootstrapdatabaseconfigure", "createalltables.sql");
    private static final Path PATH_DROP_ALL_TABLES_QUERY = Paths.get("src", "test", "resources", "bootstrapdatabaseconfigure",
            "dropalltables.sql");
    private Connection connection;
    private List<String> createAllTablesQuerys;
    private List<String> dropAllTablesQuerys;
    private List<String> createSchema;
    private List<String> dropSchema;   
    private Logger log = LoggerFactory.getLogger(getClass());
    {
        try {
            createAllTablesQuerys = new SQLScriptReaderImpl(PATH_CREATE_ALL_TABLES).read();
            dropAllTablesQuerys = new SQLScriptReaderImpl(PATH_DROP_ALL_TABLES_QUERY).read();
            createSchema = new SQLScriptReaderImpl(PATH_CREATE_SCHEMA).read();
            dropSchema = new SQLScriptReaderImpl(PATH_DROP_SCHEMA).read();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public void createAllTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
        	statement.addBatch(createSchema.get(0));
            for (String query : createAllTablesQuerys) {
                statement.addBatch(query);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error("Error occurred: {} SQLState: {} ErrorCode: {}", e.getMessage(), e.getSQLState(),
                    e.getErrorCode());
            throw new SQLException();
        }
    }
    
    @Override
    public void configure(final List<String> configureScript) throws SQLException {
        createAllTables();
        try (Statement statement = connection.createStatement()) {
            for (String query : configureScript) {
                statement.addBatch(query);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error("Error occurred: {} SQLState: {} ErrorCode: {}", e.getMessage(), e.getSQLState(),
                    e.getErrorCode());
            throw new SQLException();
        }
    }

    @Override
    public void close() throws IOException {
        try (Statement statment = connection.createStatement()) {
            for (String query : dropAllTablesQuerys) {
                statment.addBatch(query);
            }
            statment.addBatch(dropSchema.get(0));
            statment.executeBatch();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new IOException(e);
        }
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Error occurred: {} SQLState: {} ErrorCode: {}", e.getMessage(), e.getSQLState(),
                    e.getErrorCode());
            throw new IOException(e);
        }
    }
}
