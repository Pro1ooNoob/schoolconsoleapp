package ua.com.foxminded.schoolconsoleapp.dao.coursesdao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import ua.com.foxminded.schoolconsoleapp.dao.crud.cruddaointerface.CrudDAO;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.CoursesDTO;
import ua.com.foxminded.schoolconsoleapp.dto.StudentToCourseDTO;

public interface CoursesDAO extends CrudDAO<CoursesDTO,Long>{
    StudentToCourseDTO addStudentById(final Connection connection, final Long studentId, final String courseName) throws DAOException, SQLException, IOException;
    StudentToCourseDTO removeStudentById(final Connection connection, final Long studentId, final String courseName) throws DAOException, SQLException, IOException;
}
