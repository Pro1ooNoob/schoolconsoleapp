package ua.com.foxminded.schoolconsoleapp.testservises.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.PropertiesReaderImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;

@TestInstance(Lifecycle.PER_CLASS)
class TableConfiguratoImplTest {
    private static final Path SCRIPT_PATH = Paths.get("src", "test", "resources", "tableconfiguratoimpltest",
            "sqlresources", "configuredatabasefortest1.sql");
    private static final Path DB_PROPERTIES = Paths.get("src", "test", "resources", "config",
            "databaseaccessinfo.properties");
    private static final String SELECT_COURSES_NAMES = """
            SELECT course_name
            FROM school.courses;
            """;
    private List<String> script;
    private Properties properties = new Properties();
    private MyDataSource myDataSource;
    private TableConfigurator tableConfigurator;
    Logger log = LoggerFactory.getLogger(getClass());
    {
        try {
            properties = new PropertiesReaderImpl(DB_PROPERTIES).read();
            myDataSource = new MyDataSourceImpl(properties);
            script = new SQLScriptReaderImpl(SCRIPT_PATH).read();
            tableConfigurator = new TableConfiguratoImpl(myDataSource.getConnection());
        } catch (IOException | SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConfigure_AskToExecuteScript_ShouldExecuteScript() throws SQLException {
        tableConfigurator.configure(script);
        List<String> actualTableList = new ArrayList<>();
        List<String> actualCoursesTableContents = new ArrayList<>();
        List<String> expectedTableList = Arrays.asList("courses", "groups", "student_to_course", "students");
        List<String> expectedCoursesTableContents = Arrays.asList("Quantum Mechanics and Applications",
                "Entrepreneurship and Innovation Strategies", "Environmental Sustainability and Conservation",
                "Artificial Intelligence and Machine Learning");
        try (Connection connection = myDataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet resultSet = metaData.getTables(null, null, null, new String[] { "TABLE" });) {
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    if (expectedTableList.contains(tableName)) {
                        actualTableList.add(tableName);
                    }
                }
            }
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(SELECT_COURSES_NAMES)) {
                while (resultSet.next()) {
                    String course_name = resultSet.getString(1);
                    actualCoursesTableContents.add(course_name);
                }
            }
        }
        assertAll(() -> assertLinesMatch(expectedTableList, actualTableList),
                () -> assertLinesMatch(expectedCoursesTableContents, actualCoursesTableContents));
    }

    @AfterAll
    void done() throws IOException {
        tableConfigurator.close();
        myDataSource.close();
    }
}
