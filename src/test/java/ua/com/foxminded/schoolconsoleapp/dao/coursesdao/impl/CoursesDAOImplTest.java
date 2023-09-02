package ua.com.foxminded.schoolconsoleapp.dao.coursesdao.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.com.foxminded.schoolconsoleapp.dao.coursesdao.CoursesDAO;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.CoursesDTO;
import ua.com.foxminded.schoolconsoleapp.dto.StudentToCourseDTO;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;
import ua.com.foxminded.schoolconsoleapp.testservises.impl.TableConfiguratoImpl;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CoursesDAOImplTest {
	private final static String FIND_ALL_COURSES_FOR_STUDENT1 = """
			SELECT courses.course_name
			FROM school.student_to_course
			JOIN school.courses
			USING (course_id)
			WHERE student_to_course.student_id = 1;
			""";
	private final static String FIND_ALL_COURSES = """
			SELECT course_name
			FROM school.courses;
			""";
	private final static Path PATH_CONFIGURE_DATABASE_FOR_TEST1 = Paths.get("src", "test", "resources",
			"coursesdaoimpltestresources", "sqlresources", "databaseconfigure", "configuredatabasefortest1.sql");
	private final static Path PATH_CONFIGURE_DATABASE_FOR_TEST2 = Paths.get("src", "test", "resources",
			"coursesdaoimpltestresources", "sqlresources", "databaseconfigure", "configuredatabasefortest2.sql");
	private final static Path PATH_CONFIGURE_DATABASE_FOR_TEST3 = Paths.get("src", "test", "resources",
			"coursesdaoimpltestresources", "sqlresources", "databaseconfigure", "configuredatabasefortest3.sql");
	private final static Path PATH_CONFIGURE_DATABASE_FOR_TEST4 = Paths.get("src", "test", "resources",
			"coursesdaoimpltestresources", "sqlresources", "databaseconfigure", "configuredatabasefortest4.sql");
	private CoursesDAO coursesDAO = new CoursesDAOImpl();
	private List<String> test1Scripts;
	private List<String> test2Scripts;
	private List<String> test3Scripts;
	private List<String> test4Scripts;
	private String url;
	private String username;
	private String password;

	@Container
	final static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

	{
		try {
			test1Scripts = new SQLScriptReaderImpl(PATH_CONFIGURE_DATABASE_FOR_TEST1).read();
			test2Scripts = new SQLScriptReaderImpl(PATH_CONFIGURE_DATABASE_FOR_TEST2).read();
			test3Scripts = new SQLScriptReaderImpl(PATH_CONFIGURE_DATABASE_FOR_TEST3).read();
			test4Scripts = new SQLScriptReaderImpl(PATH_CONFIGURE_DATABASE_FOR_TEST4).read();

		} catch (IOException e) {
			e.printStackTrace();
			e.getMessage();
			throw new RuntimeException(e);
		}
	}

	@BeforeAll()
	void init() {
		url = container.getJdbcUrl();
		username = container.getUsername();
		password = container.getPassword();
	}

	@Test
	@Tag("1")
	void testAddStudentById_AskToAddStudentThatAttends2CoursesToNewCourse_StudentShouldBeEnrolledToCourse()
			throws IOException, SQLException, DAOException {
		final List<String> expectedCoursesList = Arrays.asList("Quantum Mechanics and Applications",
				"Entrepreneurship and Innovation Strategies", "Environmental Sustainability and Conservation");
		List<String> actualCoursesList = new ArrayList<>();
		final Long studentId = 1L;
		final Long courseId = 3L;
		final StudentToCourseDTO expectedEntity = new StudentToCourseDTO(studentId, courseId);
		StudentToCourseDTO actualEntity;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			actualEntity = coursesDAO.addStudentById(connection, 1L, "Environmental Sustainability and Conservation");
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(FIND_ALL_COURSES_FOR_STUDENT1)) {
				while (resultSet.next()) {
					final String courseName = resultSet.getString(1);
					actualCoursesList.add(courseName);
				}
			}
			assertAll(() -> assertEquals(expectedEntity, actualEntity),
					() -> assertLinesMatch(expectedCoursesList, actualCoursesList));
		}
	}

	@Test
	@Tag("2")
	void testAddStudentById_AskToAddStudentThatAttends3CoursesToNewCourse_DAOExceptoinShouldBeThrown()
			throws IOException, SQLException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test2Scripts);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				coursesDAO.addStudentById(connection, 1L, "Artificial Intelligence and Machine Learning");
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					coursesDAO.addStudentById(connection, 1L, "Artificial Intelligence and Machine Learning");
				});
				final String message = "Can't add student. The number of courses per student cannot be more than 3";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Test
	@Tag("1")
	void testAddStudentById_AskToEnrollNonExistentStudentToExistentCourse_DAOExceptoinShouldBeThrown()
			throws SQLException, IOException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				coursesDAO.addStudentById(connection, 2L, "Quantum Mechanics and Applications");
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					coursesDAO.addStudentById(connection, 2L, "Quantum Mechanics and Applications");
				});
				final String message = "Can't find student with related ID: 2";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Test
	@Tag("1")
	void testAddStudentById_AskToEnrollExistentStudentToNonExistentCourse__DAOExceptoinShouldBeThrown()
			throws SQLException, IOException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				coursesDAO.addStudentById(connection, 1L, "fake course");
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					coursesDAO.addStudentById(connection, 1L, "fake course");
				});
				final String message = "The course \"fake course\" does not exists";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Test
	@Tag("1")
	void testAddStudentById_AskToEnrollStudentToTheCourseHeHasAlreadyEnrolled_DAOExceptoinShouldBeThrown()
			throws IOException, SQLException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				coursesDAO.addStudentById(connection, 1L, "Quantum Mechanics and Applications");
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					coursesDAO.addStudentById(connection, 1L, "Quantum Mechanics and Applications");
				});
				final String message = "The student is already attending this course: \"Quantum Mechanics and Applications\"";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Test
	@Tag("1")
	void testRemoveStudentById_AskToRemoveValidStudentFromCourseHeAttends_TheRemovingShouldOccurCorrectly()
			throws IOException, SQLException, DAOException {
		final Long studentId = 1L;
		final Long courseId = 2L;
		final StudentToCourseDTO expectedEntity = new StudentToCourseDTO(studentId, courseId);
		StudentToCourseDTO actualEntity;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			final List<String> expectedList = Arrays.asList("Quantum Mechanics and Applications");
			List<String> actualList = new ArrayList<>();
			actualEntity = coursesDAO.removeStudentById(connection, 1L, "Entrepreneurship and Innovation Strategies");
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(FIND_ALL_COURSES_FOR_STUDENT1)) {
				while (resultSet.next()) {
					final String courseName = resultSet.getString(1);
					actualList.add(courseName);
				}
			}
			assertAll(() -> assertLinesMatch(expectedList, actualList),
					() -> assertEquals(expectedEntity, actualEntity));
		}
	}

	@Test
	@Tag("1")
	void testRemoveStudentById_AskToRemoveNonExistentStudentFromExistentCourses_DAOExceptoinShouldBeThrown()
			throws SQLException, IOException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				coursesDAO.removeStudentById(connection, 2L, "Quantum Mechanics and Applications");
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					coursesDAO.removeStudentById(connection, 2L, "Quantum Mechanics and Applications");
				});
				final String message = "The course name \"Quantum Mechanics and Applications\" was not found for the student ID 2";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Test
	@Tag("1")
	void testRemoveStudentById_AskToRemoveExistentStudentFromNonExistentCourses_DAOExceptoinShouldBeThrown()
			throws IOException, SQLException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				coursesDAO.removeStudentById(connection, 1L, "fake course");
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					coursesDAO.removeStudentById(connection, 1L, "fake course");
				});
				final String message = "The course name \"fake course\" was not found for the student ID 1";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Test
	@Tag("3")
	void testRemoveStudentById_AskToRemoveStudentWhichAttendsOnlyOneCourse_DAOExceptoinShouldBeThrown()
			throws SQLException, IOException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test3Scripts);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				coursesDAO.removeStudentById(connection, 1L, "Quantum Mechanics and Applications");
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					coursesDAO.removeStudentById(connection, 1L, "Quantum Mechanics and Applications");
				});
				final String message = "Can't remove student. Each student must attend at least one course";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Test
	@Tag("1")
	void testFindById_AskToFindExistedCourseById_ShouldReturnDesiredCourseEntity() throws SQLException, IOException {
		final CoursesDTO expectedEntity = new CoursesDTO(1L, "Quantum Mechanics and Applications", "course info");
		CoursesDTO actualEntity;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			actualEntity = coursesDAO.findById(connection, 1L).orElseThrow();
			assertEquals(expectedEntity, actualEntity);
		}
	}

	@Test
	@Tag("1")
	void testFindAll_InvokingMethod_ShouldReturnAllCoursesEntityList() throws SQLException, IOException {
		final CoursesDTO expectedEntity0 = new CoursesDTO(1L, "Quantum Mechanics and Applications", "course info");
		final CoursesDTO expectedEntity1 = new CoursesDTO(2L, "Entrepreneurship and Innovation Strategies",
				"course info");
		final CoursesDTO expectedEntity2 = new CoursesDTO(3L, "Environmental Sustainability and Conservation",
				"course info");
		final CoursesDTO expectedEntity3 = new CoursesDTO(4L, "Artificial Intelligence and Machine Learning",
				"course info");
		final List<CoursesDTO> expectedEntityList = Arrays.asList(expectedEntity0, expectedEntity1, expectedEntity2,
				expectedEntity3);
		List<CoursesDTO> actualEntityList = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			actualEntityList = coursesDAO.findAll(connection);
		}
		assertIterableEquals(expectedEntityList, actualEntityList);
	}

	@Test
	@Tag("1")
	void testDeleteById_AskToRemoveExistentCourseById_CourseShouldBeRemoved()
			throws SQLException, IOException, DAOException {
		final List<String> expectedList = Arrays.asList("Entrepreneurship and Innovation Strategies",
				"Environmental Sustainability and Conservation", "Artificial Intelligence and Machine Learning");
		final Long courseId = 1L;
		final String courseName = "Quantum Mechanics and Applications";
		final String courseDescription = "course info";
		final CoursesDTO expectedEntity = new CoursesDTO(courseId, courseName, courseDescription);
		final CoursesDTO actualEntity;
		List<String> actualList = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			actualEntity = coursesDAO.deleteById(connection, 1L);
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(FIND_ALL_COURSES)) {
				while (resultSet.next()) {
					final String courseName1 = resultSet.getString(1);
					actualList.add(courseName1);
				}
			}
		}
		assertAll(() -> assertIterableEquals(expectedList, actualList),
				() -> assertEquals(expectedEntity, actualEntity));
	}

	@Test
	void testCreate_AskToCreateNewCourse_NewCourseShouldBeCreated() throws SQLException, IOException {
		final List<String> expectedList = Arrays.asList("New course");
		List<String> actualList = new ArrayList<>();
		final Long courseId = 1L;
		final String courseName = "New course";
		final String courseDescription = "some info";
		final CoursesDTO passedEntity = new CoursesDTO(courseId, courseName, courseDescription);
		CoursesDTO returnedEntity;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test4Scripts);
			returnedEntity = ((CoursesDAOImpl) coursesDAO).create(connection, passedEntity);
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(FIND_ALL_COURSES)) {
				while (resultSet.next()) {
					final String courseName1 = resultSet.getString(1);
					actualList.add(courseName1);
				}
			}
		}
		assertAll(() -> assertEquals(passedEntity, returnedEntity), () -> assertLinesMatch(expectedList, actualList));
	}

	@Test
	void testUpdate_AskToUpdateCourse_CourseShouldBeUpdated() throws IOException, SQLException {
		final List<String> expectedList = Arrays.asList("Entrepreneurship and Innovation Strategies",
				"Environmental Sustainability and Conservation", "Artificial Intelligence and Machine Learning",
				"New course");
		List<String> actualList = new ArrayList<>();
		final Long courseId = 1L;
		final String courseName = "New course";
		final String courseDescription = "some info";
		final CoursesDTO passedEntity = new CoursesDTO(courseId, courseName, courseDescription);
		CoursesDTO returnedEntity;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(test1Scripts);
			returnedEntity = ((CoursesDAOImpl) coursesDAO).update(connection, passedEntity);
			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(FIND_ALL_COURSES)) {
				while (resultSet.next()) {
					final String courseName1 = resultSet.getString(1);
					actualList.add(courseName1);
				}
			}
		}
		assertAll(() -> assertEquals(passedEntity, returnedEntity), () -> assertLinesMatch(expectedList, actualList));
	}
}
