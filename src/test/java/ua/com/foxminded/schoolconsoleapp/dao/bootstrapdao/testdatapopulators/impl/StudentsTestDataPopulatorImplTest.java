package ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
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
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.TestDataPopulator;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;
import ua.com.foxminded.schoolconsoleapp.testservises.impl.TableConfiguratoImpl;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
class StudentsTestDataPopulatorImplTest {   
    private static final String SELECT_ALL_STUDENTS_QUERY = """ 
            SELECT * 
            FROM school.students;
            """;
    private String url;
    private String username;
    private String password;
    private Logger log = LoggerFactory.getLogger(getClass());
    private MyDataSource myDataSource = mock(MyDataSourceImpl.class);
    private TestDataPopulator<List<String>> populator = new StudentsTestDataPopulatorImpl(myDataSource);
    private List<String> emptyScript = new ArrayList<>();

    @Container
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");
    
    @BeforeEach
    void setup() throws SQLException {
        url = container.getJdbcUrl();
        username = container.getUsername();
        password = container.getPassword();
        when(myDataSource.getConnection()).then(invocation -> DriverManager.getConnection(url, username, password));
    }

	@ParameterizedTest
	@MethodSource("provideNotEmptyStudentsList")
	void populate_PassNotEmptyStudentsList_ShouldPopulateTableWithStudents(List<String> groupsList)
			throws SQLException, IOException {
		List<String> actualList = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(emptyScript);
			populator.populate(groupsList);
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(SELECT_ALL_STUDENTS_QUERY)) {
				while (resultSet.next()) {
					final String firstName = resultSet.getString(3);
					final String lastName = resultSet.getString(4);
					final String studentName = firstName + " " + lastName;
					actualList.add(studentName);
				}
			} catch (SQLException e) {
				String message = String.format(
						"The error occurred during selection from students table. SQLState %s, Error code %s",
						e.getSQLState(), e.getErrorCode());
				log.error(message);
				throw new SQLException(message, e);
			}
			assertIterableEquals(groupsList, actualList, "The courseList and actualList should be equal");
		}
	}

	@ParameterizedTest
	@MethodSource("provideEmptyStudentsList")
	void populate_PassEmptyStudentsList_TheTableShouldBeEmpty(List<String> groupsList)
			throws SQLException, IOException {
		List<String> actualList = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(emptyScript);
			populator.populate(groupsList);
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(SELECT_ALL_STUDENTS_QUERY)) {
				while (resultSet.next()) {
					final String firstName = resultSet.getString(3);
					final String lastName = resultSet.getString(4);
					final String studentName = firstName + " " + lastName;
					actualList.add(studentName);
				}
			} catch (SQLException e) {
				String message = String.format(
						"The error occurred during selection from students table. SQLState %s, Error code %s",
						e.getSQLState(), e.getErrorCode());
				log.error(message);
				throw new SQLException(message, e);
			}
			assertIterableEquals(groupsList, actualList, "The courseList and actualList should be equal");
		}
	}

    private static Stream<Arguments> provideNotEmptyStudentsList() {
        return Stream.of(
                Arguments.of(Arrays.asList("FirstName1 LastName1", "FirstName2 LastName2", "FirstName3 LastName3")));
    }

    private static Stream<Arguments> provideEmptyStudentsList() {
        return Stream.of(Arguments.of(Arrays.asList()));
    }
}
