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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
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
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;
import ua.com.foxminded.schoolconsoleapp.testservises.impl.TableConfiguratoImpl;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.TestDataPopulator;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
class StudentToCourseTestDataPopulatorImplTest {
    private static final Path PATH_SCRIPT = Paths.get("src", "test", "resources", "studenttocoursetestdatapopulatorimpltestresources", "sqlresources", "configurescripts", "script1.sql");
    private static final String SELECT_ALL_FROM_STUDENT_TO_COURSE_TABLE = """ 
            SELECT * 
            FROM school.student_to_course;
            """;
    private String url;
    private String username;
    private String password;
    private MyDataSource myDataSource = mock(MyDataSourceImpl.class);
    private TestDataPopulator<Map<Integer, ArrayList<Integer>>> populator = new StudentToCourseTestDataPopulatorImpl(myDataSource);
    private Logger log = LoggerFactory.getLogger(getClass());
    private List<String> script1;
    
    {
    	try {
			script1 = new SQLScriptReaderImpl(PATH_SCRIPT).read();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
    }

    @Container
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");
    
    @BeforeAll
    void init() throws IOException, SQLException {
        when(myDataSource.getConnection()).then(invocation -> DriverManager.getConnection(url, username, password));
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        url = container.getJdbcUrl();
        username = container.getUsername();
        password = container.getPassword();
    }

	@ParameterizedTest
	@MethodSource("provideNotEmptyStudentGroupList")
	void populate_PassNotEmptyStudentsList_ShouldPopulateTableWithStudents(
			Map<Integer, ArrayList<Integer>> groupsStudentMap) throws SQLException, IOException {
		Map<Integer, ArrayList<Integer>> studentToCoursesMapActual = new HashMap<>();
		ArrayList<Integer> courses = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(script1);
			populator.populate(groupsStudentMap);
			try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
					ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_STUDENT_TO_COURSE_TABLE)) {
				int k = 1;
				while (resultSet.next()) {
					Integer studentId = resultSet.getInt(1);
					Integer courseId = resultSet.getInt(2);
					if (studentId == k) {
						courses.add(courseId);
					} else {
						studentToCoursesMapActual.put(k, courses);
						k = studentId;
						courses = new ArrayList<>();
						courses.add(courseId);
					}
				}
				studentToCoursesMapActual.put(k, courses);
			} catch (SQLException e) {
				String message = String.format(
						"The error occured during selection from StudentToCourse. SQLState %s. Error code %s",
						e.getSQLState(), e.getErrorCode());
				log.error(message);
				throw new SQLException(message, e);
			}
			assertEquals(groupsStudentMap, studentToCoursesMapActual,
					"The groupsStudentMap and groupsStudentMapActual should be equal");
		}
	}
    
    private static Stream<Arguments> provideNotEmptyStudentGroupList() {
        return Stream.of(Arguments.of(Arrays.asList("1 1,2", "2 2,3", "3 3,4").stream().map(string -> {
            String[] strArr = string.split(" ");
            Integer groupId = Integer.parseInt(strArr[0]);
            ArrayList<Integer> students = Stream.of(strArr[1].split(",")).map(Integer::parseInt)
                    .collect(Collectors.toCollection(ArrayList::new));
            Map.Entry<Integer, ArrayList<Integer>> entry = Map.entry(groupId, students);
            return entry;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }
}