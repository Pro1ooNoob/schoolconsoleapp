package ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer.DDLPerformer;
import ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer.impl.DDLPerformerImpl;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.TestDataPopulator;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
class CoursesTestDataPopulatorImplTest {
    private final Path pathCreateAllTablesQuery  = Paths.get("src", "test", "resources", "bootstrapdatabaseconfigure",
            "createalltables.sql");
    private final Path pathDropAllTablesQuery = Paths.get("src", "test", "resources", "bootstrapdatabaseconfigure",
            "dropalltables.sql");
    private final Path pathCreateSchemaQuery = Paths.get("src", "test", "resources", "bootstrapdatabaseconfigure",
    		"createschema.sql");
    private final Path pathDropSchemaQuery = Paths.get("src", "test", "resources", "bootstrapdatabaseconfigure",
    		"dropalltables.sql");
    private final String SELECT_ALL_COURSES_QUERY = """
            SELECT *
            FROM school.courses;
                        """;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    private ResourceReader<List<String>> tableCreationQuerysReader = new SQLScriptReaderImpl(
            pathCreateAllTablesQuery);
    private ResourceReader<List<String>> dropAllTablesQueryReader = new SQLScriptReaderImpl(
            pathDropAllTablesQuery);
    private ResourceReader<List<String>> schemaCreationQuerysReader = new SQLScriptReaderImpl(
    		pathCreateSchemaQuery);
    private ResourceReader<List<String>> dropSchemaQueryReader = new SQLScriptReaderImpl(
    		pathDropSchemaQuery);
    private MyDataSource myDataSource1 = mock(MyDataSourceImpl.class);
    private DDLPerformer tableCreator = new DDLPerformerImpl(myDataSource1, tableCreationQuerysReader);
    private DDLPerformer tableDeletor = new DDLPerformerImpl(myDataSource1, dropAllTablesQueryReader);
    private DDLPerformer schemaCreator = new DDLPerformerImpl(myDataSource1, schemaCreationQuerysReader);
    private DDLPerformer schemaDeletor = new DDLPerformerImpl(myDataSource1, dropSchemaQueryReader);
    private TestDataPopulator<List<String>> populator = new CoursesTestDataPopulatorImpl(myDataSource1);
    private Logger log = LoggerFactory.getLogger(getClass());

    @Container
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

    @BeforeEach
    void setup() throws SQLException, IOException {
        url = container.getJdbcUrl();
        username = container.getUsername();
        password = container.getPassword();
        when(myDataSource1.getConnection()).then(invocation -> {
            connection = DriverManager.getConnection(url, username, password);
            return connection;
        });
        try {
        	schemaCreator.performDDL();
            tableCreator.performDDL();
        } catch (SQLException e) {
            String message = String.format(
                    "The exception occured during courses table creation. SQLState: %s, Error code %s", e.getSQLState(),
                    e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideNotEmptyCourseList")
    void populate_PassNotEmptyCourseList_ShouldPopulateTableWithCourses(List<String> courseList)
            throws SQLException, IOException {
        populator.populate(courseList);
        List<String> actualList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_ALL_COURSES_QUERY)) {
            while (resultSet.next()) {
                final String courseName = resultSet.getString(2);
                actualList.add(courseName);
            }
        } catch (SQLException e) {
            String message = String.format(
                    "The exception occurred during selection from courses table. SQLState: %s, Error code %s",
                    e.getSQLState(), e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
        assertIterableEquals(courseList, actualList, "The courseList and actualList should be equal");
    }

    @ParameterizedTest
    @MethodSource("provideEmptyCourseList")
    void populate_PassEmptyCourseList_TheTableShouldBeEmpty(List<String> courseList) throws SQLException, IOException {
        populator.populate(courseList);
        List<String> actualList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_ALL_COURSES_QUERY)) {
            while (resultSet.next()) {
                final String courseName = resultSet.getString(2);
                actualList.add(courseName);
            }
        } catch (SQLException e) {
            String message = String.format(
                    "The exception occurred during selection from courses table. SQLState: %s, Error code %s",
                    e.getSQLState(), e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
        assertIterableEquals(courseList, actualList, "The courseList and actualList should be equal");
    }
    
    @AfterEach
    void tearDown() throws IOException, SQLException {
        try {
            tableDeletor.performDDL();
            schemaDeletor.performDDL();
            
        } catch (SQLException e) {
            String message = String.format(
                    "The exception occured during courses table deletion. SQLState: %s, Error code %s", e.getSQLState(),
                    e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
    }

    private static Stream<Arguments> provideNotEmptyCourseList() {
        return Stream.of(Arguments.of(Arrays.asList("course 1", "course 2", "course 3")));
    }

    private static Stream<Arguments> provideEmptyCourseList() {
        return Stream.of(Arguments.of(Arrays.asList()));
    }
}
