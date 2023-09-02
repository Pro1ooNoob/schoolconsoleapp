package ua.com.foxminded.schoolconsoleapp.services.studentsservice.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dao.studentdao.StudentsDAO;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;
import ua.com.foxminded.schoolconsoleapp.services.studentsservice.StudentsService;
import static ua.com.foxminded.schoolconsoleapp.utils.transactionutils.TransactionUtils.mapTransaction;

public class StudentsServiceImpl implements StudentsService {
    private final MyDataSource dataSource;
    private final StudentsDAO studenstDAO;

    public StudentsServiceImpl(MyDataSource datasource, StudentsDAO studentDao) {
        this.dataSource = datasource;
        this.studenstDAO = studentDao;
    }

    public Optional<List<StudentsDTO>> findAllByCourseName(final String courseName)
            throws DAOException, SQLException, IOException {
        return mapTransaction(dataSource, conn -> studenstDAO.findAllByCourseName(conn, courseName));
    }

    public StudentsDTO addStudent(final String firstName, final String lastName)
            throws DAOException, SQLException, IOException {
        return mapTransaction(dataSource, conn -> studenstDAO.addStudent(conn, firstName, lastName));
    }

    public StudentsDTO addStudent(final Long groupId, final String firstName, final String lastName)
            throws DAOException, SQLException, IOException {
        return mapTransaction(dataSource, conn -> studenstDAO.addStudent(conn, groupId, firstName, lastName));
    }

    public StudentsDTO deleteById(Long id) throws SQLException, DAOException {
        return mapTransaction(dataSource, conn -> studenstDAO.deleteById(conn, id));
    }
}
