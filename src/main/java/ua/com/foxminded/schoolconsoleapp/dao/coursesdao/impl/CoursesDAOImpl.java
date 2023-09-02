package ua.com.foxminded.schoolconsoleapp.dao.coursesdao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.dao.coursesdao.CoursesDAO;
import ua.com.foxminded.schoolconsoleapp.dao.crud.AbstractCrudDao;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.CoursesDTO;
import ua.com.foxminded.schoolconsoleapp.dto.StudentToCourseDTO;
import ua.com.foxminded.schoolconsoleapp.functionalinterfaces.ResultSetMapper;

public class CoursesDAOImpl extends AbstractCrudDao<CoursesDTO, Long> implements CoursesDAO {

    private static final String ADD_STUDENT_BY_ID_QUERY = """      
            INSERT INTO school.student_to_course (student_id, course_id) VALUES (?, (
            SELECT course_id 
            FROM school.courses 
            WHERE course_name = ?
            ))
            RETURNING *;
            """;
    private static final String DELETE_STUDENT_BY_ID_QUERY = """
            DELETE FROM school.student_to_course 
            WHERE student_id = ? AND course_id = (
            SELECT course_id 
            FROM school.courses 
            WHERE course_name = ?
            )
            RETURNING *;
            """;
    private static final String FIND_STUDENT_ID_AND_RELATED_COURSE_NAMES_QUERY = """
            SELECT students.student_id, courses.course_name
            FROM school.students
            JOIN school.student_to_course
            USING(student_id)
            JOIN school.courses
            USING(course_id)
            WHERE student_id = ?;
            """;
    private static final String SELECT_AMOUNT_OF_COURSES_FOR_STUDENT_QUERY = """
            SELECT COUNT(course_id)
            FROM school.student_to_course
            WHERE student_id = ?;
            """;
    private static final String FIND_ALL_COURSES_RELATED_TO_STUDENT_ID_QUERY = """
            SELECT student_to_course.student_id, courses.course_name
            FROM school.student_to_course
            JOIN school.courses
            USING (course_id)
            WHERE student_to_course.student_id = ?;
            """;
    private static final String FIND_STUDENT_BY_STUDENT_ID_QUERY = """
            SELECT student_id
            FROM school.students
            WHERE student_id = ?;
            """;
    private static final String FIND_COURSE_BY_ID = """ 
            SELECT * 
            FROM school.courses
            WHERE course_id = ?;
            """;
    private static final String FIND_ALL_COURSES = """ 
            SELECT * 
            FROM school.courses;
            """;
    private static final String DELETE_COURSE_BY_ID = """ 
            DELETE 
            FROM school.courses
            WHERE course_id = ?
            RETURNING *;
            """; 
   private static final String INSERT_COURSE = """ 
           INSERT INTO school.courses (course_name, course_description) VALUES (?, ?)
           RETURNING *;
           """;
   private static final String UPDATE_COURSE = """
           UPDATE school.courses 
           SET 
           course_name = ?,
           course_description = ?
           WHERE course_id = ?
           RETURNING *;
           """;
   private static final String FIND_SPECIFIC_COURSE = """ 
           SELECT course_id 
           FROM school.courses
           WHERE course_name = ?;
           """;
   
    private Logger log = LoggerFactory.getLogger(getClass());
    private ResultSetMapper<StudentToCourseDTO> rsMapperStudentToCourse = rs -> {
        final Long studentId = rs.getLong(1);
        final Long courseId = rs.getLong(2);
        return new StudentToCourseDTO(studentId, courseId);
    };
    private ResultSetMapper<CoursesDTO> rsMapperCourses = rs -> {
        final Long courseId = rs.getLong(1);
        final String courseName = rs.getString(2);
        final String courseDescription = rs.getString(3);
        return new CoursesDTO(courseId,courseName, courseDescription);
    };

    @Override
    public StudentToCourseDTO addStudentById(final Connection connection, final Long studentId, final String courseName)
            throws DAOException, SQLException, IOException {
        StudentToCourseDTO studentToCourseDTO;
        final String studentAttendsCourseMessage = "The student is already attending this course: \"%s\"";
        final String studentAdditionError = "The query%n%s%s The error occurred during addition student to course. SQLState %s, Error code %s";
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_STUDENT_ID_QUERY)) {
            preparedStatement.setLong(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    String message = String.format("Can't find student with related ID: %d", studentId);
                    throw new DAOException(message);
                }
            } catch (SQLException e) {
                if (isUniqueConstraintViolation(e)) {
                    String message = String.format(studentAttendsCourseMessage, courseName);
                    throw new DAOException(message);
                } else {
                    String message = String.format(studentAdditionError, FIND_STUDENT_BY_STUDENT_ID_QUERY,
                            e.getMessage(), e.getSQLState(), e.getErrorCode());
                    log.error(message);
                    throw new SQLException(message, e);
                }
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_SPECIFIC_COURSE)) {
            preparedStatement.setString(1, courseName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    String message = String.format("The course \"%s\" does not exists", courseName);
                    throw new DAOException(message);
                }
            } catch (SQLException e) {
                String message = String.format(studentAdditionError, FIND_SPECIFIC_COURSE, e.getMessage(),
                        e.getSQLState(), e.getErrorCode());
                log.error(message);
                throw new SQLException(message, e);
            }
        }

        try (PreparedStatement preparedStatement = connection
                .prepareStatement(SELECT_AMOUNT_OF_COURSES_FOR_STUDENT_QUERY)) {
            preparedStatement.setInt(1, 1);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                int amountCoursesForStudent = resultSet.getInt(1);

                if (amountCoursesForStudent == 3) {
                    throw new DAOException(
                            "Can't add student. The number of courses per student cannot be more than 3");
                }
            }
        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                String message = String.format(studentAttendsCourseMessage, courseName);
                throw new DAOException(message);
            } else {
                String message = String.format(studentAdditionError, SELECT_AMOUNT_OF_COURSES_FOR_STUDENT_QUERY,
                        e.getMessage(), e.getSQLState(), e.getErrorCode());
                log.error(message);
                throw new SQLException(message, e);
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_STUDENT_BY_ID_QUERY)) {
            preparedStatement.setLong(1, studentId);
            preparedStatement.setString(2, courseName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                studentToCourseDTO = rsMapperStudentToCourse.map(resultSet);
            }
        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                String message = String.format(studentAttendsCourseMessage, courseName);
                throw new DAOException(message);
            } else {
                String message = String.format(studentAdditionError, ADD_STUDENT_BY_ID_QUERY, e.getMessage(),
                        e.getSQLState(), e.getErrorCode());
                log.error(message);
                throw new SQLException(message, e);
            }
        }
        return studentToCourseDTO;
    }

    @Override
    public StudentToCourseDTO removeStudentById(final Connection connection, final Long studentId,
            final String courseName) throws DAOException, SQLException, IOException {
        boolean studentIdNotFound = true;
        boolean courseNameNotFound = true;
        StudentToCourseDTO resultEntity = new StudentToCourseDTO(studentId, studentId);
        final String studentRemovingError = "The query %s%nThe error occured during removing student from course. SQLState %s, Error code %s";

        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_STUDENT_ID_QUERY)) {
            preparedStatement.setLong(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    studentIdNotFound = false;
                }
            }
        } catch (SQLException e) {
            String message = String.format(studentRemovingError, FIND_STUDENT_BY_STUDENT_ID_QUERY, e.getSQLState(),
                    e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                FIND_STUDENT_ID_AND_RELATED_COURSE_NAMES_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            preparedStatement.setLong(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String courseName1 = resultSet.getString(2);
                    if (Objects.equals(courseName, courseName1)) {
                        courseNameNotFound = false;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            String message = String.format(studentRemovingError, FIND_STUDENT_ID_AND_RELATED_COURSE_NAMES_QUERY,
                    e.getSQLState(), e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }

        if ((!studentIdNotFound && courseNameNotFound) || studentIdNotFound) {
            String message = String.format("The course name \"%s\" was not found for the student ID %d", courseName,
                    studentId);
            throw new DAOException(message);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                FIND_ALL_COURSES_RELATED_TO_STUDENT_ID_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {
            preparedStatement.setLong(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.last();
                int lastRowIndex = resultSet.getRow();
                String courseName1 = resultSet.getString(2);
                if (lastRowIndex == 1 && Objects.equals(courseName, courseName1)) {
                    throw new DAOException("Can't remove student. Each student must attend at least one course");
                }
            }
        } catch (SQLException e) {
            String message = String.format(studentRemovingError, FIND_ALL_COURSES_RELATED_TO_STUDENT_ID_QUERY,
                    e.getSQLState(), e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT_BY_ID_QUERY)) {
            preparedStatement.setLong(1, studentId);
            preparedStatement.setString(2, courseName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                resultEntity = rsMapperStudentToCourse.map(resultSet);
            }
        } catch (SQLException e) {
            String message = String.format(studentRemovingError, DELETE_STUDENT_BY_ID_QUERY, e.getSQLState(),
                    e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
        return resultEntity;
    }

    @Override
    public Optional<CoursesDTO> findById(Connection connection, Long id) throws SQLException {
        CoursesDTO coursesDTO = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_COURSE_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    coursesDTO = rsMapperCourses.map(resultSet);
                }
            }
        }
        return Optional.ofNullable(coursesDTO);
    }

    @Override
    public List<CoursesDTO> findAll(Connection connection) throws SQLException {
        List<CoursesDTO> courses = new ArrayList<>();
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(FIND_ALL_COURSES)) {
            while (resultSet.next()) {
                CoursesDTO coursesDTO = rsMapperCourses.map(resultSet);
                courses.add(coursesDTO);
            }
        }
        return courses;
    }

    @Override
    public CoursesDTO deleteById(Connection connection, Long id) throws SQLException {
        CoursesDTO coursesDTO;
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COURSE_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                coursesDTO = rsMapperCourses.map(resultSet);
            }
        }
        return coursesDTO;
    }

    @Override
    protected CoursesDTO create(Connection connection, CoursesDTO entity) throws SQLException {
        CoursesDTO resultEntity;
        final String courseName = entity.getCourseName();
        final String courseDescription = entity.getCourseDescription();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COURSE)) {
            preparedStatement.setString(1, courseName);
            preparedStatement.setString(2, courseDescription);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                resultEntity = rsMapperCourses.map(resultSet);
            }
        }
        return resultEntity;
    }

    @Override
    protected CoursesDTO update(Connection connection, CoursesDTO entity) throws SQLException {
        CoursesDTO resultEntity;
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_COURSE)) {
            final Long id = entity.getId();
            final String courseName = entity.getCourseName();
            final String courseDescription = entity.getCourseDescription();
            preparedStatement.setString(1, courseName);
            preparedStatement.setString(2, courseDescription);
            preparedStatement.setLong(3, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                resultEntity = rsMapperCourses.map(resultSet);
            }
        }
        return resultEntity;
    }
    
    private boolean isUniqueConstraintViolation(SQLException e) {
        // Check specific SQLState or error codes associated with unique constraint
        // violations
        return "23505".equals(e.getSQLState()) || e.getErrorCode() == 2627 || e.getErrorCode() == 2601;
    }
}
