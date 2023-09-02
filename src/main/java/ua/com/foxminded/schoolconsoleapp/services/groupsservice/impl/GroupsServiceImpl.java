package ua.com.foxminded.schoolconsoleapp.services.groupsservice.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import ua.com.foxminded.schoolconsoleapp.dao.groupdao.GroupsDAO;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;
import ua.com.foxminded.schoolconsoleapp.services.groupsservice.GroupsService;
import static ua.com.foxminded.schoolconsoleapp.utils.transactionutils.TransactionUtils.mapTransaction;

public class GroupsServiceImpl implements GroupsService {
    private MyDataSource myDataSource;
    private GroupsDAO groupsDAO;

    public GroupsServiceImpl(MyDataSource myDataSource, GroupsDAO groupsDAO) {
        this.myDataSource = myDataSource;
        this.groupsDAO = groupsDAO;
    }

    @Override
    public Optional<List<GroupsDTO>> findAllWithLessOrEqualStudents(Long studentsAmount)
            throws SQLException, IOException {
        return mapTransaction(myDataSource,
                connection -> groupsDAO.findAllWithLessOrEqualStudents(connection, studentsAmount));
    }
}
