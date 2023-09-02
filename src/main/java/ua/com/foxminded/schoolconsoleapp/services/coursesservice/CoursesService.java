package ua.com.foxminded.schoolconsoleapp.services.coursesservice;

import java.io.IOException;
import java.sql.SQLException;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.StudentToCourseDTO;

public interface CoursesService {
    StudentToCourseDTO addStudentById(final Long studentId, final String courseName) throws DAOException, SQLException, IOException;
    StudentToCourseDTO removeStudentById(final Long studentId, final String courseName) throws DAOException, SQLException, IOException;
}
