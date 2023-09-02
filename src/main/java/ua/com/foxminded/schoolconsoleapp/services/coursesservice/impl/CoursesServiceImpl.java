package ua.com.foxminded.schoolconsoleapp.services.coursesservice.impl;

import java.io.IOException;
import java.sql.SQLException;
import ua.com.foxminded.schoolconsoleapp.dao.coursesdao.CoursesDAO;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.dto.StudentToCourseDTO;
import ua.com.foxminded.schoolconsoleapp.services.coursesservice.CoursesService;
import static ua.com.foxminded.schoolconsoleapp.utils.transactionutils.TransactionUtils.mapTransaction;

public class CoursesServiceImpl implements CoursesService {
    private MyDataSource myDataSource;
    private CoursesDAO coursesDAO;
    
    public CoursesServiceImpl(MyDataSource myDataSource, CoursesDAO coursesDAO) {
        this.myDataSource = myDataSource;
        this.coursesDAO = coursesDAO;
    }

    @Override
    public StudentToCourseDTO addStudentById(Long studentId, String courseName) throws DAOException, SQLException, IOException {
         return mapTransaction(myDataSource, conn -> coursesDAO.addStudentById(conn, studentId, courseName));
    }

    @Override
    public StudentToCourseDTO removeStudentById(Long studentId, String courseName) throws DAOException, SQLException, IOException {
        return mapTransaction(myDataSource, conn -> coursesDAO.addStudentById(conn, studentId, courseName));
    }
}
