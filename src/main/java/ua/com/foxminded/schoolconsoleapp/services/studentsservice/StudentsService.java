package ua.com.foxminded.schoolconsoleapp.services.studentsservice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;

public interface StudentsService {
    Optional<List<StudentsDTO>>findAllByCourseName(final String courseName) throws DAOException, SQLException, IOException;
    StudentsDTO addStudent(final String firstName, final String lastName) throws DAOException, SQLException, IOException;
    StudentsDTO addStudent(final Long groupId, final String firstName, final String lastName) throws DAOException, SQLException, IOException;
    StudentsDTO deleteById(Long id) throws SQLException, DAOException;
}
