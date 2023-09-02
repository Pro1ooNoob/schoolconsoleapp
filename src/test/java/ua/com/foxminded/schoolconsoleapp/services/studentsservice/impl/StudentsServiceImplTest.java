package ua.com.foxminded.schoolconsoleapp.services.studentsservice.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dao.studentdao.StudentsDAO;
import ua.com.foxminded.schoolconsoleapp.dao.studentdao.impl.StudentsDAOImpl;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.services.studentsservice.StudentsService;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;
import ua.com.foxminded.schoolconsoleapp.testservises.impl.TableConfiguratoImpl;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
class StudentsServiceImplTest {
	private static final Path PATH_SCRIPT = Paths.get("src", "test", "resources", "studentsserviceimpltest", "scripts",
			"script.sql");
	private String url;
	private String username;
	private String password;
	private StudentsDAO studentsDAO = new StudentsDAOImpl();
	private MyDataSource myDataSource = mock(MyDataSourceImpl.class);
	private StudentsService studentService;
	private List<String> script;

	@Container
	final static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

	@BeforeEach
	void setUp() throws Exception {
		studentService = new StudentsServiceImpl(myDataSource, studentsDAO);
		script = new SQLScriptReaderImpl(PATH_SCRIPT).read();
		url = container.getJdbcUrl();
		username = container.getUsername();
		password = container.getPassword();
		when(myDataSource.getConnection()).then((invocation) -> DriverManager.getConnection(url, username, password));
	}

	@Test
	void testFindAllByCourseName_AskToFindAllByCourseName_ShouldReturnAllListOfStudentsRelatedToCourse()
			throws IOException, SQLException, DAOException {
		final StudentsDTO studentsDTO0 = new StudentsDTO(1L, 0L, "firstName1", "lastName1");
		final StudentsDTO studentsDTO1 = new StudentsDTO(2L, 0L, "firstName2", "lastName2");
		final StudentsDTO studentsDTO2 = new StudentsDTO(3L, 0L, "firstName3", "lastName3");
		final String courseName = "course1";
		final List<StudentsDTO> expectedList = Arrays.asList(studentsDTO0, studentsDTO1, studentsDTO2);
		List<StudentsDTO> actualList;
		try (TableConfigurator tableConfigurator = new TableConfiguratoImpl(myDataSource.getConnection())) {
			tableConfigurator.configure(script);
			actualList = studentService.findAllByCourseName(courseName).orElseThrow();
		}
		assertEquals(expectedList, actualList);
	}

	@Test
	void testAddStudent_AskToAddStudentUsingFirstNameLastName_StudentShouldBeAdded()
			throws IOException, SQLException, DAOException {
		final String firstName = "firstName12";
		final String lastName = "lastName12";
		final StudentsDTO expectedEntity = new StudentsDTO(12L, 0L, firstName, lastName);
		StudentsDTO actualEntity;
		try (TableConfigurator tableConfigurator = new TableConfiguratoImpl(myDataSource.getConnection())) {
			tableConfigurator.configure(script);
			actualEntity = studentService.addStudent(firstName, lastName);
		}
		assertEquals(expectedEntity, actualEntity);
	}

	@Test
	void testAddStudent_AskToAddStudentUsingFirstNameLastNameGroupId_StudentShouldBeAdded()
			throws IOException, SQLException, DAOException {
		final Long groupId = 1L;
		final String firstName = "firstName12";
		final String lastName = "lastName12";
		final StudentsDTO expectedEntity = new StudentsDTO(12L, groupId, firstName, lastName);
		StudentsDTO actualEntity;
		try (TableConfigurator tableConfigurator = new TableConfiguratoImpl(myDataSource.getConnection())) {
			tableConfigurator.configure(script);
			actualEntity = studentService.addStudent(groupId, firstName, lastName);
		}
		assertEquals(expectedEntity, actualEntity);
	}

	@Test
	void testDeleteById_AskToDeleteStudentById_StudentShouldBeDeleted() throws SQLException, DAOException, IOException {
		final Long groupId = 11L;
		final String firstName = "firstName11";
		final String lastName = "lastName11";
		final StudentsDTO expectedEntity = new StudentsDTO(11L, 0L, firstName, lastName);
		final StudentsDTO actualEntity;
		try (TableConfigurator tableConfigurator = new TableConfiguratoImpl(myDataSource.getConnection())) {
			tableConfigurator.configure(script);
			actualEntity = studentService.deleteById(groupId);
		}
		assertEquals(expectedEntity, actualEntity);
	}
}
