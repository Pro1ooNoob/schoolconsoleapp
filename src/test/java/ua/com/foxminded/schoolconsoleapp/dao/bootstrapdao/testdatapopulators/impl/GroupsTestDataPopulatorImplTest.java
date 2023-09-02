package ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
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
class GroupsTestDataPopulatorImplTest {
	private String url;
	private String username;
	private String password;
	private static final String SELECT_ALL_FROM_GROUPS_QUERY = """
			SELECT *
			FROM school.groups;
			""";
	private final List<String> emptyScript = new ArrayList<String>();
	private MyDataSource myDataSource = mock(MyDataSourceImpl.class);
	private TestDataPopulator<List<String>> populator = new GroupsTestDataPopulatorImpl(myDataSource);
	private Logger log = LoggerFactory.getLogger(getClass());

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
	@MethodSource("provideNotEmptyGroupsList")
	void populate_PassNotEmptyGroupsList_ShouldPopulateTableWithGroups(List<String> groupsList)
			throws SQLException, IOException {
		List<String> actualList = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(emptyScript);
			populator.populate(groupsList);
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_GROUPS_QUERY)) {
				while (resultSet.next()) {
					final String groupName = resultSet.getString(2);
					actualList.add(groupName);
				}
			} catch (SQLException e) {
				String message = String.format(
						"The error occured during selection from groups table. SQLState %s, Error code %s",
						e.getSQLState(), e.getErrorCode());
				log.error(message);
				throw new SQLException(message, e);
			}
		}
		assertIterableEquals(groupsList, actualList, "The courseList and actualList should be equal");
	}

	@ParameterizedTest
	@MethodSource("provideEmptyGroupsList")
	void populate_PassEmptyGroupsList_TheTableShouldBeEmpty(List<String> groupsList) throws SQLException, IOException {
		List<String> actualList = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(emptyScript);
			populator.populate(groupsList);
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_GROUPS_QUERY)) {
				while (resultSet.next()) {
					final String groupName = resultSet.getString(2);
					actualList.add(groupName);
				}
			} catch (SQLException e) {
				String message = String.format(
						"The error occured during selection from groups table. SQLState %s, Error code %s",
						e.getSQLState(), e.getErrorCode());
				log.error(message);
				throw new SQLException(message, e);
			}
			assertIterableEquals(groupsList, actualList, "The courseList and actualList should be equal");
		}
	}

	private static Stream<Arguments> provideNotEmptyGroupsList() {
		return Stream.of(Arguments.of(Arrays.asList("DW-42", "DO-95", "OM-75")));
	}

	private static Stream<Arguments> provideEmptyGroupsList() {
		return Stream.of(Arguments.of(Arrays.asList()));
	}
}
