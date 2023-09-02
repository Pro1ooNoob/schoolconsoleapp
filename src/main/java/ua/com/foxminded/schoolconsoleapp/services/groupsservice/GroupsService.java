package ua.com.foxminded.schoolconsoleapp.services.groupsservice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;

public interface GroupsService {
    Optional<List<GroupsDTO>> findAllWithLessOrEqualStudents(final Long studentsAmount) throws SQLException, IOException;
}
