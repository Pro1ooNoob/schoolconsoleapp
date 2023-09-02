package ua.com.foxminded.schoolconsoleapp.services.coursesservice.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.com.foxminded.schoolconsoleapp.dao.coursesdao.CoursesDAO;
import ua.com.foxminded.schoolconsoleapp.dao.coursesdao.impl.CoursesDAOImpl;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.dto.StudentToCourseDTO;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;
import ua.com.foxminded.schoolconsoleapp.testservises.impl.TableConfiguratoImpl;
import ua.com.foxminded.schoolconsoleapp.services.coursesservice.CoursesService;

@Testcontainers
class CoursesServiceImplTest {
    private final static Path PATH_CONFIGURE_DATABASE_FOR_TEST1 = Paths.get("src", "test", "resources",
            "coursesdaoimpltestresources", "sqlresources", "databaseconfigure", "configuredatabasefortest1.sql");
    private final static String FIND_ALL_COURSES_FOR_STUDENT1 = """
            SELECT courses.course_name
            FROM school.student_to_course
            JOIN school.courses
            USING (course_id)
            WHERE student_to_course.student_id = 1;
            """;
    private String url;
    private String username;
    private String password;
    private List<String> test1Scripts;
    private MyDataSource myDataSource = mock(MyDataSourceImpl.class);
    private CoursesDAO coursesDAO = new CoursesDAOImpl();
    private CoursesService coursesService = new CoursesServiceImpl(myDataSource, coursesDAO);
    {
        try {
            test1Scripts = new SQLScriptReaderImpl(PATH_CONFIGURE_DATABASE_FOR_TEST1).read(); 
        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
            throw new RuntimeException(e);
        }
    }
    
    @Container
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");
    
    @BeforeEach()
    void init() throws SQLException {
        url = container.getJdbcUrl();
        username = container.getUsername();
        password = container.getPassword();
        when(myDataSource.getConnection()).then(invocation -> {
            return DriverManager.getConnection(url, username, password);
        });
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
            actualEntity = coursesService.addStudentById(1L,
                    "Environmental Sustainability and Conservation");
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(FIND_ALL_COURSES_FOR_STUDENT1)) {
                while (resultSet.next()) {
                    final String courseName = resultSet.getString(1);
                    actualCoursesList.add(courseName);
                }
            }
            assertAll(
                    () -> assertEquals(expectedEntity, actualEntity),
                    () -> assertLinesMatch(expectedCoursesList, actualCoursesList)
                    );
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
            actualEntity = coursesDAO.removeStudentById(connection, 1L,
                    "Entrepreneurship and Innovation Strategies");
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(FIND_ALL_COURSES_FOR_STUDENT1)) {
                while (resultSet.next()) {
                    final String courseName = resultSet.getString(1);
                    actualList.add(courseName);
                }
            }
            assertAll(
                    () -> assertLinesMatch(expectedList, actualList),
                    () -> assertEquals(expectedEntity, actualEntity)
            );
        }
    }
}
