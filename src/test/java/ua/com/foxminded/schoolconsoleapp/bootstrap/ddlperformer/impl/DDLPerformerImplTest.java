package ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer.impl;

import static org.junit.jupiter.api.Assertions.assertAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer.DDLPerformer;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.PropertiesReaderImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class DDLPerformerImplTest {
    private String dropAllTablesQuery;
    private static final Logger log = LoggerFactory.getLogger(DDLPerformerImpl.class);
    private final Path pathDatabaseAccessInfoProperties = Paths.get("src", "test", "resources", "config",
            "databaseaccessinfo.properties");
    private final Path pathCreateAllTablesQuery  = Paths.get("src", "test", "resources", "bootstrapdatabaseconfigure",
            "createalltables.sql");
    private final Path pathDropAllTablesQuery = Paths.get("src", "test", "resources", "bootstrapdatabaseconfigure",
            "dropalltables.sql");
    private static final Path PATH_CREATE_SCHEMA = Paths.get("src", "main", "resources", "bootstrapdatabaseconfigure",
            "createschema.sql");
    private static final Path PATH_DROP_SCHEMA = Paths.get("src", "main", "resources", "bootstrapdatabaseconfigure",
            "dropschema.sql");
    private ResourceReader<Properties> propertiesReader = new PropertiesReaderImpl(pathDatabaseAccessInfoProperties);
    private Properties properties = new Properties();
    private Connection connection;
    private MyDataSource myDataSource1;
    private MyDataSource myDataSource = mock(MyDataSourceImpl.class);
    private ResourceReader<List<String>> createAllTablesQueryReader = mock(SQLScriptReaderImpl.class);
    private ResourceReader<List<String>> schemaCreatorQueryReader = new SQLScriptReaderImpl(PATH_CREATE_SCHEMA);
    private ResourceReader<List<String>> schemaDeletorQueryReader = new SQLScriptReaderImpl(PATH_DROP_SCHEMA);  
    private DDLPerformer tableCreator = new DDLPerformerImpl(myDataSource, createAllTablesQueryReader);
    private DDLPerformer schemaCreator = new DDLPerformerImpl(myDataSource, schemaCreatorQueryReader);
    private DDLPerformer schemaDeletor = new DDLPerformerImpl(myDataSource, schemaDeletorQueryReader);
    
    @BeforeAll
    void init() throws SQLException, FileNotFoundException, IOException {  
        ResourceReader<List<String>> reader1 = new SQLScriptReaderImpl(pathDropAllTablesQuery);
        List<String> tableDropQuerys = reader1.read();
        dropAllTablesQuery = tableDropQuerys.stream().collect(Collectors.joining("\n"));
        properties = propertiesReader.read();
        myDataSource1 = new MyDataSourceImpl(properties);
        
        when(myDataSource.getConnection()).then((invocation) -> {
            connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),properties.getProperty("db.username"),properties.getProperty("db.password"));
                return connection;   
        });
    	schemaCreator.performDDL(); 
        ResourceReader<List<String>> reader = new SQLScriptReaderImpl(pathCreateAllTablesQuery);
        List<String> tableCreationQuerys  = reader.read();
        when(createAllTablesQueryReader.read()).thenReturn(tableCreationQuerys);  
    }
    
    @BeforeEach
    void setup() throws SQLException, IOException {
        try (Connection connection = myDataSource1.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropAllTablesQuery);
        }
    }
    
    @Test
    void testToCreateCoursesCatabaseUsingCreateMethod() throws SQLException, IOException {
        tableCreator.performDDL();

        try (Connection connection = DriverManager.getConnection(properties.getProperty("db.url"),
                properties.getProperty("db.username"), properties.getProperty("db.password"));
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM school.courses")) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            DatabaseMetaData metaData1 = connection.getMetaData();
            String primaryKeyColumn;
            List<String> schemas = new ArrayList<String>();

            try (ResultSet resultSet1 = metaData1.getPrimaryKeys(null, "school", "courses")) {
                resultSet1.next();
                primaryKeyColumn = resultSet1.getString("COLUMN_NAME");
            }

            try (ResultSet resultSet1 = metaData1.getSchemas()) {
                while (resultSet1.next()) {
                    schemas.add(resultSet1.getString(1));
                }
            }

            assumingThat(!connection.isClosed(), () -> {
                assertAll("The connection has been performed. Asserts:",
                        () -> assertEquals(3, metaData.getColumnCount(), "Column amount should be 3"),
                        () -> assertEquals("course_id", metaData.getColumnName(1), "course_id column should exists"),
                        () -> assertEquals("course_name", metaData.getColumnName(2),
                                "course_name column should exists"),
                        () -> assertEquals("course_description", metaData.getColumnName(3),
                                "course_description column should exists"),
                        () -> assertEquals("serial", metaData.getColumnTypeName(1),
                                "The course_id column type should be serial"),
                        () -> assertEquals("text", metaData.getColumnTypeName(2),
                                "The course_name column type should be text"),
                        () -> assertEquals("text", metaData.getColumnTypeName(3),
                                "The course_description column type should be text"),
                        () -> assertTrue(metaData.isAutoIncrement(1),
                                "The course_id column should be AutoIncrementable"),
                        () -> assertTrue("course_id".equalsIgnoreCase(primaryKeyColumn),
                                "The course_id column should be PrimaryKey"),
                        () -> assertEquals("school",
                                schemas.stream().filter(string -> string.equalsIgnoreCase("school")).findFirst()
                                        .orElse("No schema"),
                                "The table should belongs to school schema"));
            });

        } catch (SQLException e) {
            log.error("The error occured when trying get metadata about a table ", e);
            throw new SQLException("The error occured when trying get metadata about a table ", e);
        }
    }
    
    @AfterAll
    void tearDown() throws SQLException, IOException {
        try (Connection connection = myDataSource1.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropAllTablesQuery);
        }
        schemaDeletor.performDDL();
    }
}