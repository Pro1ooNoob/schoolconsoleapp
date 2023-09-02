package ua.com.foxminded.schoolconsoleapp.dao.studentdao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;

public interface StudentsDAO {
    Optional<List<StudentsDTO>>findAllByCourseName(final Connection connection, final String courseName) throws DAOException, SQLException, IOException;
    StudentsDTO addStudent(final Connection connection, final String firstName, final String lastName) throws DAOException, SQLException, IOException;
    StudentsDTO addStudent(final Connection connection, final Long groupId, final String firstName, final String lastName) throws DAOException, SQLException, IOException;
    StudentsDTO deleteById(Connection connection, Long id) throws SQLException, DAOException;
}
