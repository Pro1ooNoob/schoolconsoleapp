package ua.com.foxminded.schoolconsoleapp.dao.groupdao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.schoolconsoleapp.dao.crud.cruddaointerface.CrudDAO;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;

public interface GroupsDAO extends CrudDAO<GroupsDTO, Long>{
    Optional<List<GroupsDTO>> findAllWithLessOrEqualStudents(final Connection connection, final Long studentsAmount) throws SQLException, IOException;
}
