package ua.com.foxminded.schoolconsoleapp.dao.groupdao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dao.groupdao.GroupsDAO;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.PropertiesReaderImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;
import ua.com.foxminded.schoolconsoleapp.testservises.impl.TableConfiguratoImpl;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
class GroupsDAOImplTest {
    private static final String SELECT_ALL_GROUPS = """
            SELECT group_id, group_name
            FROM school.groups;
            """;
    private final Path pathDBProperties = Paths.get("src", "test", "resources", "config",
            "databaseaccessinfo.properties");
    private final Path pathConfigureDatabaseForTest1 = Paths.get("src", "test", "resources", "groupsdaoimplresources",
            "sqlresources", "groupsdaoimpltesttestdatabaseconfigure", "configuredatabasefortest1.sql");
    private Properties dbProperties = new Properties();
    private String url;
    private String username;
    private String password;
    private List<String> scriptList = new ArrayList<>();
    
    @Container
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

    {
        try {
            scriptList = new SQLScriptReaderImpl(pathConfigureDatabaseForTest1).read();
            dbProperties = new PropertiesReaderImpl(pathDBProperties).read();
        } catch (IOException e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        url = dbProperties.getProperty("db.url");
        username = dbProperties.getProperty("db.username");
        password = dbProperties.getProperty("db.password");
    }

    @ParameterizedTest
    @ValueSource(longs = { 1L, 2L, 3L })
    void findAllWithLessOrEqualStudents_AskToSelectGroupsWithSpecifiedAmountOfStudents_ShouldReturnAppropriateGroups(
            Long amount) throws SQLException, IOException {
        GroupsDAO groupsDAO = new GroupsDAOImpl();
        try (Connection connection = DriverManager.getConnection(url, username, password);
                TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
            tableConfigurator.configure(scriptList);
            List<GroupsDTO> groupsActual = groupsDAO.findAllWithLessOrEqualStudents(connection, amount).orElseThrow();

            assumingThat(amount == 1, () -> {
                GroupsDTO groupDTOExpected = new GroupsDTO(1l, "AA-11");
                GroupsDTO groupDTOActual = groupsActual.get(0);
                assertAll(() -> assertEquals(1, groupsActual.size(), "The is only one group with 1 student"),
                        () -> assertEquals(groupDTOExpected, groupDTOActual,
                                "The groupDTOExpected and groupDTOActual should be equal for 1 student"));
            });
            assumingThat(amount == 2, () -> {
                GroupsDTO groupDTOExpected = new GroupsDTO(1l, "AA-11");
                GroupsDTO groupDTOExpected1 = new GroupsDTO(2l, "BB-22");
                List<GroupsDTO> groupsExpected1 = Stream.of(groupDTOExpected, groupDTOExpected1).toList();
                assertAll(() -> assertEquals(2, groupsActual.size(), "The are 2 groups with 1 or 2 students"),
                        () -> assertIterableEquals(groupsExpected1, groupsActual,
                                "The groupDTOExpected1 and groupDTOActual should be equal for 2 students"));
            });
            assumingThat(amount == 3, () -> {
                GroupsDTO groupDTOExpected = new GroupsDTO(1l, "AA-11");
                GroupsDTO groupDTOExpected1 = new GroupsDTO(2l, "BB-22");
                GroupsDTO groupDTOExpected2 = new GroupsDTO(3l, "CC-33");
                List<GroupsDTO> groupsExpected1 = Stream.of(groupDTOExpected, groupDTOExpected1, groupDTOExpected2)
                        .toList();
                assertAll(() -> assertEquals(3, groupsActual.size(), "The are 3 groups with 1, 2 or 3 students"),
                        () -> assertIterableEquals(groupsExpected1, groupsActual,
                                "The groupDTOExpected1 and groupDTOActual should be equal for 3 students"));
            });
        }
    }

    @Test
    void testFindById_AskToReturnGroupsById_ShouldReturnCorrectGroup() throws IOException, SQLException {
        GroupsDAO groupsDAO = new GroupsDAOImpl();
        final GroupsDTO expectedEntity = new GroupsDTO(1l, "AA-11");
        GroupsDTO actualEntity;
        try (Connection connection = DriverManager.getConnection(url, username, password);
                TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
            tableConfigurator.configure(scriptList);
            actualEntity = groupsDAO.findById(connection, 1l).orElseThrow();
        }
        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    void testFindAll_AskToReturnAllGroups_ShouldReturnAllGroups() throws IOException, SQLException {
        GroupsDAO groupsDAO = new GroupsDAOImpl();
        final GroupsDTO expectedEntity0 = new GroupsDTO(1l, "AA-11");
        final GroupsDTO expectedEntity1 = new GroupsDTO(2l, "BB-22");
        final GroupsDTO expectedEntity2 = new GroupsDTO(3l, "CC-33");
        final List<GroupsDTO> expectedEntityList = Arrays.asList(expectedEntity0, expectedEntity1, expectedEntity2);
        List<GroupsDTO> actualEntityList;
        try (Connection connection = DriverManager.getConnection(url, username, password);
                TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
            tableConfigurator.configure(scriptList);
            actualEntityList = groupsDAO.findAll(connection);
        }
        assertIterableEquals(expectedEntityList, actualEntityList);
    }

    @Test
    void testSave_AskToCreateNewEntity_NewEntityShouldBeCreated() throws IOException, SQLException {
        GroupsDAO groupsDAO = new GroupsDAOImpl();
        final GroupsDTO expectedEntity0 = new GroupsDTO(1l, "AA-11");
        final GroupsDTO expectedEntity1 = new GroupsDTO(2l, "BB-22");
        final GroupsDTO expectedEntity2 = new GroupsDTO(3l, "CC-33");
        final GroupsDTO expectedEntity3New = new GroupsDTO(4l, "DD-44");
        final List<GroupsDTO> expectedEntityList = Arrays.asList(expectedEntity0, expectedEntity1, expectedEntity2,
                expectedEntity3New);
        List<GroupsDTO> actualEntityList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
                TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
            tableConfigurator.configure(scriptList);
            GroupsDTO newEntity = ((GroupsDAOImpl) groupsDAO).create(connection, expectedEntity3New);
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(SELECT_ALL_GROUPS)) {
                while (resultSet.next()) {
                    final Long groupId = resultSet.getLong(1);
                    final String groupName = resultSet.getString(2);
                    GroupsDTO entity = new GroupsDTO(groupId, groupName);
                    actualEntityList.add(entity);
                }
            }
            assertAll(() -> assertEquals(expectedEntity3New, newEntity),
                    () -> assertIterableEquals(expectedEntityList, actualEntityList));
        }
    }

    @Test
    void testSave_AskToUpdateEntity_EntityShouldBeUpdated() throws IOException, SQLException, DAOException {
        GroupsDAO groupsDAO = new GroupsDAOImpl();
        final GroupsDTO expectedEntity0 = new GroupsDTO(1l, "AA-11");
        final GroupsDTO expectedEntity1 = new GroupsDTO(2l, "BB-22");
        final GroupsDTO expectedEntity2Updated = new GroupsDTO(3l, "II-77");
        final List<GroupsDTO> expectedEntityList = Arrays.asList(expectedEntity0, expectedEntity1,
                expectedEntity2Updated);
        List<GroupsDTO> actualEntityList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
                TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
            tableConfigurator.configure(scriptList);
            GroupsDTO newEntity = ((GroupsDAOImpl) groupsDAO).save(connection, expectedEntity2Updated);
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(SELECT_ALL_GROUPS)) {
                while (resultSet.next()) {
                    final Long groupId = resultSet.getLong(1);
                    final String groupName = resultSet.getString(2);
                    GroupsDTO entity = new GroupsDTO(groupId, groupName);
                    actualEntityList.add(entity);
                }
                assertAll(() -> assertEquals(expectedEntity2Updated, newEntity),
                        () -> assertIterableEquals(expectedEntityList, actualEntityList));
            }
        }
    }

    @Test
    void deleteById_AskToDeleteGroup_GroupShouldBeDeleted() throws SQLException, IOException, DAOException {
        GroupsDAO groupsDAO = new GroupsDAOImpl();
        GroupsDTO expectedGroupsDAO = new GroupsDTO(1L, "AA-11");
        GroupsDTO actualGroupsDAO;
        try (Connection connection = DriverManager.getConnection(url, username, password);
                TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
            tableConfigurator.configure(scriptList);
            actualGroupsDAO = groupsDAO.deleteById(connection, 1L);
        }
        assertEquals(expectedGroupsDAO, actualGroupsDAO);
    }
}
