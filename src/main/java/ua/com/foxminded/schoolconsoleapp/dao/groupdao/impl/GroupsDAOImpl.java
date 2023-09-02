package ua.com.foxminded.schoolconsoleapp.dao.groupdao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.dao.crud.AbstractCrudDao;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dao.groupdao.GroupsDAO;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;
import ua.com.foxminded.schoolconsoleapp.functionalinterfaces.ResultSetMapper;

public class GroupsDAOImpl extends AbstractCrudDao<GroupsDTO, Long> implements GroupsDAO {
    private static final String SELECT_GROUPS_WHERE_LESS_OR_EQUAL_STUDENT_NUMBER_QUERY = """
            SELECT school.groups.group_id, school.groups.group_name
            FROM school.groups
            WHERE group_id IN (
                SELECT school.groups.group_id
                FROM school.groups
                JOIN school.students
                USING (group_id)
                GROUP BY (school.groups.group_id)
                HAVING COUNT(*) <= ?
            );
            """;
    private static final String SELECT_GROUPS_BY_ID = """ 
            SELECT * 
            FROM school.groups
            WHERE group_id = ?
            """;
    private static final String SELECT_ALL_FROM_GROUPS = """ 
            SELECT *
            FROM school.groups;
            """;
    private static final String DELETE_GROUP_BY_ID = """ 
            DELETE 
            FROM school.groups
            WHERE group_id = ?
            RETURNING *;
            """;
    private static final String INSERT_NEW_GROUP = """ 
            INSERT INTO school.groups (group_name) 
            VALUES (?)
            RETURNING *;
            """;
    private static final String UPDATE_GROUP = """ 
            UPDATE 
            school.groups
            SET 
            group_name = ?
            WHERE group_id = ?
            RETURNING *;
            """;
    private Logger log = LoggerFactory.getLogger(getClass());

    ResultSetMapper<GroupsDTO> rsMapper = rs -> {
        final Long groupId = rs.getLong(1);
        final String groupName = rs.getString(2);
        return new GroupsDTO(groupId, groupName);
    };
    
    @Override
    public Optional<List<GroupsDTO>> findAllWithLessOrEqualStudents(final Connection connection,
            final Long studentsAmount) throws SQLException, IOException {
        List<GroupsDTO> resultList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection
                .prepareStatement(SELECT_GROUPS_WHERE_LESS_OR_EQUAL_STUDENT_NUMBER_QUERY)) {
            preparedStatement.setLong(1, studentsAmount);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    GroupsDTO groupDTO = rsMapper.map(resultSet);
                    resultList.add(groupDTO);
                }
            }
        } catch (SQLException e) {
            String message = String.format(
                    "The query%n%s%s%nThe error occurred during students selection. SQLState %s, Error code %s",
                    SELECT_GROUPS_WHERE_LESS_OR_EQUAL_STUDENT_NUMBER_QUERY, e.getMessage(), e.getSQLState(),
                    e.getErrorCode());
            log.error(message);
            throw new SQLException(message, e);
        }
        return Optional.ofNullable(resultList);
    }

    @Override
    public Optional<GroupsDTO> findById(final Connection connection, final Long id) throws SQLException {
        GroupsDTO groupsDTO = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GROUPS_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                groupsDTO = rsMapper.map(resultSet);
            }
        }
        return Optional.ofNullable(groupsDTO);
    }

    @Override
    public List<GroupsDTO> findAll(final Connection connection) throws SQLException {
        List<GroupsDTO> resultList = new ArrayList<>();
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_GROUPS)){
            while (resultSet.next()) {
                GroupsDTO entity = rsMapper.map(resultSet);
                resultList.add(entity);
            }
        }
        return resultList;
    }

    @Override
    public GroupsDTO deleteById(final Connection connection, final Long id) throws SQLException, DAOException {
        GroupsDTO resultEntity;
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GROUP_BY_ID)){
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                resultEntity = rsMapper.map(resultSet);
            }
        }
        return resultEntity;
    }

    @Override
    protected GroupsDTO create(final Connection connection, final GroupsDTO entity) throws SQLException {
        GroupsDTO groupsDTO;
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_GROUP)) {
            final String groupName = entity.getGroupName();
            preparedStatement.setString(1,groupName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                groupsDTO = rsMapper.map(resultSet);
            }
        }
        return groupsDTO;
    }

    @Override
    protected GroupsDTO update(final Connection connection, final GroupsDTO entity) throws SQLException {
        GroupsDTO groupsDTO;
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GROUP)) {
            final String groupName = entity.getGroupName();
            final Long groupId = entity.getId();
            preparedStatement.setString(1, groupName);
            preparedStatement.setLong(2, groupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                groupsDTO = rsMapper.map(resultSet);
            }
        }
        return groupsDTO;
    }    
}
