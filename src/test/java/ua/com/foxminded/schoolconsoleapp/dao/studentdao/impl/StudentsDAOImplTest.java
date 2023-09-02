package ua.com.foxminded.schoolconsoleapp.dao.studentdao.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dao.studentdao.StudentsDAO;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;
import ua.com.foxminded.schoolconsoleapp.testservises.impl.TableConfiguratoImpl;
import ua.com.foxminded.schoolconsoleapp.functionalinterfaces.*;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class StudentsDAOImplTest {
	private final Path pathScript1 = Paths.get("src", "test", "resources", "studentsdaoimpltestresources",
			"sqlresources", "databaseconfigure", "script1.sql");
	private final Path pathScript2 = Paths.get("src", "test", "resources", "studentsdaoimpltestresources",
			"sqlresources", "databaseconfigure", "script2.sql");
	private final Path pathScript3 = Paths.get("src", "test", "resources", "studentsdaoimpltestresources",
			"sqlresources", "databaseconfigure", "script3.sql");

	private static final String FIND_STUDENT_BY_ID = """
			SELECT *
			FROM school.students
			WHERE student_id = ?;
			""";

	private String url;
	private String username;
	private String password;
	private StudentsDAO studentsDAO = new StudentsDAOImpl();
	private List<String> script1 = new ArrayList<>();
	private List<String> script2 = new ArrayList<>();
	private List<String> script3 = new ArrayList<>();
	private final ResultSetMapper<StudentsDTO> rsMapper = rs -> {
		final Long studentId = rs.getLong(1);
		final Long groupId = rs.getLong(2);
		final String firstName = rs.getString(3);
		final String lastName = rs.getString(4);
		return new StudentsDTO(studentId, groupId, firstName, lastName);
	};

	@Container
	PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

	{
		try {
			script1 = new SQLScriptReaderImpl(pathScript1).read();
			script2 = new SQLScriptReaderImpl(pathScript2).read();
			script3 = new SQLScriptReaderImpl(pathScript3).read();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@BeforeEach
	void setup() {
		url = container.getJdbcUrl();
		username = container.getUsername();
		password = container.getPassword();
	}

	@Tag("1")
	@Test
	void findAllByCourseName_AskToReturnAllStudentsRelatedToSpecificCourse_ShouldReturnListStudentDTO()
			throws DAOException, SQLException, IOException {
		final String courseName1 = "Quantum Mechanics and Applications";
		final StudentsDTO entity0 = new StudentsDTO(1L, 0L, "firstName1", "lastName1");
		final StudentsDTO entity1 = new StudentsDTO(2L, 0L, "firstName2", "lastName2");
		final StudentsDTO entity2 = new StudentsDTO(3L, 0L, "firstName3", "lastName3");
		final List<StudentsDTO> expectedList = Arrays.asList(entity0, entity1, entity2);
		List<StudentsDTO> actualList = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script1);
			actualList = studentsDAO.findAllByCourseName(connection, courseName1).orElseThrow();
		}
		assertIterableEquals(expectedList, actualList);
	}

	@Tag("1")
	@Test
	void findAllByCourseName_AskToReturnStudentsRelatedToNonExistingCourse_ShouldThrowDAOException()
			throws SQLException, IOException {
		final String courseName1 = "fake course";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script1);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				studentsDAO.findAllByCourseName(connection, courseName1);
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					studentsDAO.findAllByCourseName(connection, courseName1);
				});
				final String message = "The course \"fake course\" doesn't exists";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Tag("2")
	@Test
	void addStudent_InsertStudentUsingGroupIdifGroupContains10Students_TheStudentShouldBeInserted()
			throws DAOException, SQLException, IOException {
		final Long groupId = 1L;
		final String firstName = "firstName11";
		final String lastName = "lastName11";
		StudentsDTO expectedStudentsDTO = new StudentsDTO(11L, 1L, "firstName11", "lastName11");
		StudentsDTO actualStudentDTO;
		StudentsDTO newEntity;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script2);
			newEntity = studentsDAO.addStudent(connection, groupId, firstName, lastName);
			try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_ID)) {
				preparedStatement.setLong(1, 11);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					resultSet.next();
					actualStudentDTO = rsMapper.map(resultSet);
				}
			}
		}
		assertAll(() -> assertEquals(expectedStudentsDTO, actualStudentDTO),
				() -> assertEquals(expectedStudentsDTO, newEntity));
	}

	@Tag("3")
	@Test
	void addStudent_InsertStudentUsingGroupIdifGroupContains30Students_ShouldBeThrownDAOException()
			throws DAOException, SQLException, IOException {
		final Long groupId = 1L;
		final String firstName = "firstName31";
		final String lastName = "lastName31";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(script3);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				studentsDAO.addStudent(connection, groupId, firstName, lastName);
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					studentsDAO.addStudent(connection, groupId, firstName, lastName);
				});
				final String message = "Can't assign student. The group by ID 1 already contains 30 students";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Tag("1")
	@Test
	void addStudent_InsertStudentUsingNonExistentGroupId_ShouldBeThrownDAOException()
			throws DAOException, SQLException, IOException {
		final Long groupId = -1L;
		final String firstName = "firstName-1";
		final String lastName = "lastName-1";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script1);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				studentsDAO.addStudent(connection, groupId, firstName, lastName);
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					studentsDAO.addStudent(connection, groupId, firstName, lastName);
				});
				final String message = "The group with id -1 wasn't found";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Tag("1")
	@Test
	void addStudent_InsertStudentUsingFirstAndLastName_TheStudentShouldBeInsertedAndEntityShouldBeReturned()
			throws DAOException, SQLException, IOException {
		final StudentsDTO expectedStudent = new StudentsDTO(4L, 0L, "firstName4", "lastName4");
		StudentsDTO returnedEntity;
		StudentsDTO actualEntity;
		final String firstName = "firstName4";
		final String lastName = "lastName4";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(
						DriverManager.getConnection(url, username, password))) {
			tableConfigurator.configure(script1);
			returnedEntity = studentsDAO.addStudent(connection, firstName, lastName);
			Long studentId = returnedEntity.getId();
			try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_ID)) {
				preparedStatement.setLong(1, studentId);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					resultSet.next();
					actualEntity = rsMapper.map(resultSet);
				}
			}
		}
		assertAll(() -> assertEquals(expectedStudent, returnedEntity),
				() -> assertEquals(expectedStudent, actualEntity));
	}

	@Tag("1")
	@Test
	void deleteByID_DeleteExistingStudent_ShouldDeleteStudent() throws DAOException, SQLException, IOException {
		final Long studentId = 1L;
		final Long groupId = 0L;
		final String firstName = "firstName1";
		final String lastName = "lastName1";
		StudentsDTO expectedEntity = new StudentsDTO(studentId, groupId, firstName, lastName);
		StudentsDTO returnedEntity;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script1);
			returnedEntity = studentsDAO.deleteById(connection, 1L);
			try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_ID)) {
				preparedStatement.setLong(1, 1L);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					assertTrue(!resultSet.next());
				}
			}
		}
		assertEquals(expectedEntity, returnedEntity);
	}

	@Tag("1")
	@Test
	void deleteByID_DeleteNonExistingStudent_ShouldThrowDAOException() throws IOException, SQLException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script1);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				studentsDAO.deleteById(connection, -1L);
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					studentsDAO.deleteById(connection, -1L);
				});
				final String message = "The student by ID -1 doesn't exists";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Tag("1")
	@Test
	void findById_AskToFindStudentById_CorrectStudentShouldBeReturned() throws IOException, SQLException {
		StudentsDTO actualEntity = null;
		StudentsDTO expectedEntity = new StudentsDTO(1L, 0L, "firstName1", "lastName1");
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script1);
			actualEntity = ((StudentsDAOImpl) studentsDAO).findById(connection, 1L).orElseThrow();
		}
		assertEquals(expectedEntity, actualEntity);
	}

	@Tag("1")
	@Test
	void findAll_AskToFindAllStudents_AllStudentsShouldBeReturned() throws SQLException, IOException {
		final StudentsDTO entity0 = new StudentsDTO(1L, 0L, "firstName1", "lastName1");
		final StudentsDTO entity1 = new StudentsDTO(2L, 0L, "firstName2", "lastName2");
		final StudentsDTO entity2 = new StudentsDTO(3L, 0L, "firstName3", "lastName3");
		List<StudentsDTO> expectedList = Arrays.asList(entity0, entity1, entity2);
		List<StudentsDTO> actualList;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script1);
			actualList = ((StudentsDAOImpl) studentsDAO).findAll(connection);
		}
		assertIterableEquals(expectedList, actualList);
	}

	@Tag("2")
	@Test
	void create_InsertStudentUsingGroupIdifGroupContains10Students_TheStudentShouldBeInserted()
			throws DAOException, SQLException, IOException {
		final Long studentId = 11L;
		final Long groupId = 1L;
		final String firstName = "firstName11";
		final String lastName = "lastName11";
		StudentsDTO expectedStudentsDTO = new StudentsDTO(studentId, groupId, firstName, lastName);
		StudentsDTO passedEntity = new StudentsDTO(groupId, firstName, lastName);
		StudentsDTO actualStudentDTOReturned;
		StudentsDTO actualStudentDTOFromDatabase;

		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script2);
			actualStudentDTOReturned = ((StudentsDAOImpl) studentsDAO).create(connection, passedEntity);
			try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_ID)) {
				preparedStatement.setLong(1, 11);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					resultSet.next();
					actualStudentDTOFromDatabase = rsMapper.map(resultSet);
				}
			}
		}
		assertAll(() -> assertEquals(expectedStudentsDTO, actualStudentDTOReturned),
				() -> assertEquals(expectedStudentsDTO, actualStudentDTOFromDatabase));
	}

	@Tag("3")
	@Test
	void create_InsertStudentUsingGroupIdifGroupContains30Students_ShouldBeThrownDAOException()
			throws DAOException, SQLException, IOException {
		final Long groupId = 1L;
		final String firstName = "firstName31";
		final String lastName = "lastName31";
		final StudentsDTO passedEntity = new StudentsDTO(groupId, firstName, lastName);
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(script3);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				((StudentsDAOImpl) studentsDAO).create(connection, passedEntity);
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					((StudentsDAOImpl) studentsDAO).create(connection, passedEntity);
				});
				final String message = "Can't assign student. The group by ID 1 already contains 30 students";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Tag("1")
	@Test
	void create_InsertStudentUsingNonExistentGroupId_ShouldBeThrownDAOException()
			throws DAOException, SQLException, IOException {
		final Long groupId = -1L;
		final String firstName = "firstName-1";
		final String lastName = "lastName-1";
		final StudentsDTO passedEntity = new StudentsDTO(groupId, firstName, lastName);
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator configurator = new TableConfiguratoImpl(connection)) {
			configurator.configure(script1);
			assertAll(() -> assertThrows(DAOException.class, () -> {
				((StudentsDAOImpl) studentsDAO).create(connection, passedEntity);
			}), () -> {
				Throwable throwable = assertThrows(DAOException.class, () -> {
					((StudentsDAOImpl) studentsDAO).create(connection, passedEntity);
				});
				final String message = "The group with id -1 wasn't found";
				assertEquals(message, throwable.getMessage());
			});
		}
	}

	@Tag("1")
	@Test
	void create_InsertStudentUsingFirstAndLastName_TheStudentShouldBeInsertedAndEntityShouldBeReturned()
			throws DAOException, SQLException, IOException {
		final StudentsDTO expectedStudent = new StudentsDTO(4L, 0L, "firstName4", "lastName4");
		final String firstName = "firstName4";
		final String lastName = "lastName4";
		final StudentsDTO passedEntity = new StudentsDTO(firstName, lastName);
		StudentsDTO actualStudentDTOReturned;
		StudentsDTO actualStudentDTOFromDatabase;

		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(script1);
			actualStudentDTOReturned = ((StudentsDAOImpl) studentsDAO).create(connection, passedEntity);
			Long studentId = actualStudentDTOReturned.getId();
			try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_ID)) {
				preparedStatement.setLong(1, studentId);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					resultSet.next();
					actualStudentDTOFromDatabase = rsMapper.map(resultSet);
				}
			}
		}
		assertAll(() -> assertEquals(expectedStudent, actualStudentDTOReturned),
				() -> assertEquals(expectedStudent, actualStudentDTOFromDatabase));
	}

	@Tag("2")
	@Test
	void update_UpdateStudentNotUsingGroupIdifGroupContains30Students_TheStudentShouldBeUpdated()
			throws DAOException, SQLException, IOException {
		final Long studentId = 10L;
		final Long groupId = 1L;
		final String firstName = "newFirstName";
		final String lastName = "newLastName";
		final StudentsDTO passedEntity = new StudentsDTO(studentId, groupId, firstName, lastName);
		StudentsDTO actualStudentDTOReturned;
		try (Connection connection = DriverManager.getConnection(url, username, password);
				TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
			tableConfigurator.configure(script2);
			actualStudentDTOReturned = ((StudentsDAOImpl) studentsDAO).update(connection, passedEntity);
			assertEquals(passedEntity, actualStudentDTOReturned);
		}
	}
}
