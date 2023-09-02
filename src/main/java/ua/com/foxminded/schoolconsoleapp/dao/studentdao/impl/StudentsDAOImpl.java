package ua.com.foxminded.schoolconsoleapp.dao.studentdao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ua.com.foxminded.schoolconsoleapp.dao.crud.AbstractCrudDao;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dao.studentdao.StudentsDAO;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;
import ua.com.foxminded.schoolconsoleapp.functionalinterfaces.*;

public class StudentsDAOImpl extends AbstractCrudDao<StudentsDTO, Long> implements StudentsDAO {
    private static final String FIND_STUDENTS_RELATED_TO_COURSE_QUERY = """
            SELECT subquery2.student_id, subquery2.group_id, subquery2.first_name, subquery2.last_name
            FROM (
                SELECT st.student_id, st.group_id, st.first_name, st.last_name
                FROM school.students AS st
                JOIN (
                    SELECT *
                    FROM school.student_to_course
                ) AS subquery ON (subquery.student_id = st.student_id)
                JOIN (
                    SELECT *
                    FROM school.courses
                    WHERE course_name = ?
                ) AS subquery1 ON subquery1.course_id = subquery.course_id
            ) AS subquery2;
            """;
    private static final String FIND_COURSE_BY_NAME = """
            SELECT *
            FROM school.courses
            WHERE course_name = ?;
            """;
    private static final String COUNT_ALL_STUDENTS_IN_GROUP = """
            SELECT COUNT(*)
            FROM school.students
            JOIN school.groups
            USING (group_id)
            WHERE group_id = ?;
            """;
    private static final String FIND_GROUP_BY_ID = """
            SELECT *
            FROM school.groups
            WHERE group_id = ?;
            """;
    private static final String INSERT_RETURN_STUDENT_USING_GROUP_ID = """
            INSERT INTO school.students (group_id, first_name, last_name) VALUES (?, ?, ?) RETURNING *;
            """;
    private static final String INSERT_RETURN_STUDENT_BY_NAME = """
            INSERT INTO school.students (first_name, last_name) VALUES (?, ?) RETURNING *;
            """;
    private static final String FIND_STUDENT_BY_ID = """
            SELECT *
            FROM school.students
            WHERE student_id = ?;
            """;
    private static final String DELETED_STUDENT_BY_ID = """
            DELETE
            FROM school.students
            WHERE student_id = ?
            RETURNING *;
            """;
    private static final String FIND_ALL_STUDENTS = """
            SELECT *
            FROM school.students;
            """;
    private static final String UPDATE_STUDENT = """
            UPDATE school.students
            SET
            group_id = ?,
            first_name = ?,
            last_name = ?
            WHERE student_id = ?
            RETURNING *;
            """;
    private final ResultSetMapper<StudentsDTO> rsMapper = rs -> {
        final Long studentId = rs.getLong(1);
        final Long groupId = rs.getLong(2);
        final String firstName = rs.getString(3);
        final String lastName = rs.getString(4);
        return new StudentsDTO(studentId, groupId, firstName, lastName);
    };

    @Override
    public Optional<StudentsDTO> findById(Connection connection, Long id) throws SQLException {
        StudentsDTO resultEntity = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    resultEntity = rsMapper.map(resultSet);
                }
            }
        }
        return Optional.of(resultEntity);
    }

    @Override
    public List<StudentsDTO> findAll(Connection connection) throws SQLException {
        List<StudentsDTO> resultList = new ArrayList<>();
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(FIND_ALL_STUDENTS)) {
            while (resultSet.next()) {
                StudentsDTO entity = rsMapper.map(resultSet);
                resultList.add(entity);
            }
        }
        return resultList;
    }

    @Override
    public Optional<List<StudentsDTO>> findAllByCourseName(Connection connection, String courseName)
            throws DAOException, SQLException, IOException {
        List<StudentsDTO> resultList = new ArrayList<>();
        checkIfCourseExists(connection, courseName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENTS_RELATED_TO_COURSE_QUERY)) {
            preparedStatement.setString(1, courseName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    StudentsDTO studentDTO = rsMapper.map(resultSet);
                    resultList.add(studentDTO);
                }
            }
        }
        return Optional.of(resultList);
    }

    @Override
    public StudentsDTO addStudent(Connection connection, String firstName, String lastName)
            throws DAOException, SQLException, IOException {
        StudentsDTO resultEntity = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RETURN_STUDENT_BY_NAME)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    resultEntity = rsMapper.map(resultSet);
                }
            }
        }
        return resultEntity;
    }

    @Override
    public StudentsDTO addStudent(Connection connection, Long groupId, String firstName, String lastName)
            throws DAOException, SQLException, IOException {
        StudentsDTO resultEntity = null;
        checkIfAmountStudentsInGroupLessThen30(connection, groupId);
        checkIfGroupExists(connection, groupId);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RETURN_STUDENT_USING_GROUP_ID)) {
            preparedStatement.setLong(1, groupId);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    resultEntity = rsMapper.map(resultSet);
                }
            }
        }

        return resultEntity;
    }

    @Override
    protected StudentsDTO create(Connection connection, StudentsDTO entity) throws SQLException, DAOException {
        final Long groupId = entity.getGroupId();
        final String firstName = entity.getFirstName();
        final String lastName = entity.getLastName();
        StudentsDTO resultEntity;
        if (groupId != null) {
            checkIfGroupExists(connection, groupId);
            checkIfAmountStudentsInGroupLessThen30(connection, groupId);
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RETURN_STUDENT_USING_GROUP_ID)) {
            if (groupId == null) {
                preparedStatement.setNull(1, Types.INTEGER);
            } else {
                preparedStatement.setLong(1, groupId);
            }
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                resultEntity = rsMapper.map(resultSet);
            }
        }
        return resultEntity;
    }

    @Override
    protected StudentsDTO update(Connection connection, StudentsDTO entity) throws SQLException, DAOException {
        StudentsDTO studentDTO;
        final Long studentId = entity.getId();
        final Long groupId = entity.getGroupId();
        final String firstName = entity.getFirstName();
        final String lastName = entity.getLastName();
        checkIfGroupExists(connection, groupId);
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT)) {
            if (groupId == null) {
                preparedStatement.setNull(1, Types.INTEGER);
            } else {
                preparedStatement.setLong(1, groupId);
            }
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setLong(4, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                studentDTO = rsMapper.map(resultSet);
            }
        }
        return studentDTO;
    }

    @Override
    public StudentsDTO deleteById(Connection connection, Long studentId) throws SQLException, DAOException {
        StudentsDTO studentsDTO;
        checkIfStudentExists(connection, studentId);
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETED_STUDENT_BY_ID)) {
            preparedStatement.setLong(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                studentsDTO = rsMapper.map(resultSet);
            }
        }
        return studentsDTO;
    }

    void checkIfCourseExists(Connection connection, String courseName) throws SQLException, DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_COURSE_BY_NAME)) {
            preparedStatement.setString(1, courseName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    String message = String.format("The course \"%s\" doesn't exists", courseName);
                    throw new DAOException(message);
                }
            }
        }
    }

    void checkIfStudentExists(Connection connection, Long studentId) throws SQLException, DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_BY_ID)) {
            preparedStatement.setLong(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    final String message = String.format("The student by ID %d doesn't exists", studentId);
                    throw new DAOException(message);
                }
            }
        }
    }

    void checkIfGroupExists(Connection connection, Long groupId) throws SQLException, DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_GROUP_BY_ID)) {
            preparedStatement.setLong(1, groupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    final String message = String.format("The group with id %d wasn't found", groupId);
                    throw new DAOException(message);
                }
            }
        }
    }

    void checkIfAmountStudentsInGroupLessThen30(Connection connection, Long groupId) throws SQLException, DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(COUNT_ALL_STUDENTS_IN_GROUP)) {
            preparedStatement.setLong(1, groupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                final int amount = resultSet.getInt(1);
                if (amount == 30) {
                    final String message = String
                            .format("Can't assign student. The group by ID %d already contains 30 students", groupId);
                    throw new DAOException(message);
                }
            }
        }
    }
}
