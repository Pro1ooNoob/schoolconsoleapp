package ua.com.foxminded.schoolconsoleapp.services.groupsservice.impl;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import ua.com.foxminded.schoolconsoleapp.dao.groupdao.GroupsDAO;
import ua.com.foxminded.schoolconsoleapp.dao.groupdao.impl.GroupsDAOImpl;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.services.groupsservice.GroupsService;
import ua.com.foxminded.schoolconsoleapp.testservises.TableConfigurator;
import ua.com.foxminded.schoolconsoleapp.testservises.impl.TableConfiguratoImpl;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class GroupsServiceImplTest {
    private static final Path PATH_SCRIPT1 = Paths.get("src", "test", "resources", "groupsserviceImpltestresources", "scripts","script1.sql");
    private String url;
    private String username;
    private String password;
    private List<String> script;
    private GroupsService groupsService;
    private GroupsDAO groupsDAO = new GroupsDAOImpl();
    private MyDataSource myDataSource = mock(MyDataSourceImpl.class);
    
    @Container
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");
    
    {
        try {
            script = new SQLScriptReaderImpl(PATH_SCRIPT1).read();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        groupsService = new GroupsServiceImpl(myDataSource, groupsDAO);
    }
    
    
    @BeforeEach
    void init() throws SQLException {
    	url = container.getJdbcUrl();
    	username = container.getUsername();
    	password = container.getPassword();
        when(myDataSource.getConnection()).then(invocation -> DriverManager.getConnection(url, username, password));
    }
    
    @Test
    void testFindAllWithLessOrEqualStudents_AskToFindGroupsWithGivenConditions_ShouldReturnGroupsList() throws IOException, SQLException {
        final Long studentsAmount = 3L;
        final GroupsDTO entity0 = new GroupsDTO(1L, "AA-22");
        final List<GroupsDTO> expectedList = Arrays.asList(entity0);
        List<GroupsDTO> actualList;
        try (Connection connection = DriverManager.getConnection(url, username, password); 
                TableConfigurator tableConfigurator = new TableConfiguratoImpl(connection)) {
            tableConfigurator.configure(script);
            actualList = groupsService.findAllWithLessOrEqualStudents(studentsAmount).orElseThrow();
        }
        assertIterableEquals(expectedList, actualList);
    }
}
