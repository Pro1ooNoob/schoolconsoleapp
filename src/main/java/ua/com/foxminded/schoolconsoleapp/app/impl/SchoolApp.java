package ua.com.foxminded.schoolconsoleapp.app.impl;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl.StudentToGroupAllocatorImpl;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl.StudentsTestDataPopulatorImpl;
import ua.com.foxminded.schoolconsoleapp.app.SchoolAppAbstract;
import ua.com.foxminded.schoolconsoleapp.app.menuservice.MyConsole;
import ua.com.foxminded.schoolconsoleapp.app.menuservice.impl.MyConsoleImpl;
import ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer.DDLPerformer;
import ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer.impl.DDLPerformerImpl;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatagenerator.TestDataGenerator;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatageneratorsabstractfactory.TestDataGeneratorsAbstractFactory;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.TestDataPopulator;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl.CoursesTestDataPopulatorImpl;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl.GroupsTestDataPopulatorImpl;
import ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators.impl.StudentToCourseTestDataPopulatorImpl;
import ua.com.foxminded.schoolconsoleapp.dao.coursesdao.CoursesDAO;
import ua.com.foxminded.schoolconsoleapp.dao.coursesdao.impl.CoursesDAOImpl;
import ua.com.foxminded.schoolconsoleapp.dao.groupdao.GroupsDAO;
import ua.com.foxminded.schoolconsoleapp.dao.groupdao.impl.GroupsDAOImpl;
import ua.com.foxminded.schoolconsoleapp.dao.studentdao.StudentsDAO;
import ua.com.foxminded.schoolconsoleapp.dao.studentdao.impl.StudentsDAOImpl;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.impl.MyDataSourceImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.PropertiesReaderImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SQLScriptReaderImpl;
import ua.com.foxminded.schoolconsoleapp.services.coursesservice.CoursesService;
import ua.com.foxminded.schoolconsoleapp.services.coursesservice.impl.CoursesServiceImpl;
import ua.com.foxminded.schoolconsoleapp.services.groupsservice.GroupsService;
import ua.com.foxminded.schoolconsoleapp.services.groupsservice.impl.GroupsServiceImpl;
import ua.com.foxminded.schoolconsoleapp.services.studentsservice.StudentsService;
import ua.com.foxminded.schoolconsoleapp.services.studentsservice.impl.StudentsServiceImpl;

public class SchoolApp extends SchoolAppAbstract implements Closeable {
	private static final Path PATH_DATABASE_ACCESS_INFO = Paths.get("src", "main", "resources", "config",
			"databaseaccessinfo.properties");
	private static final Path PATH_CREATE_ALL_TABLES_QUERY = Paths.get("src", "main", "resources",
			"bootstrapdatabaseconfigure", "createalltables.sql");
	private static final Path PATH_DROP_ALL_TABLES_QUERY = Paths.get("src", "main", "resources",
			"bootstrapdatabaseconfigure", "dropalltables.sql");
	private static final Path PATH_CREATE_SCHEMA = Paths.get("src", "main", "resources", "bootstrapdatabaseconfigure",
			"createschema.sql");
	private static final Path PATH_DROP_SCHEMA = Paths.get("src", "main", "resources", "bootstrapdatabaseconfigure",
			"dropschema.sql");

	private MyDataSource myDataSource;
	private static GroupsDAO groupsDAO;
	private static StudentsDAO studentsDAO;
	private static CoursesDAO coursesDAO;
	private static GroupsService groupsService;
	private static StudentsService studentsService;
	private static CoursesService coursesService;
	private static MyConsole console;
	private static ResourceReader<List<String>> createAllTablesQueryReader;
	private static ResourceReader<List<String>> dropAllTablesQueryReader;
	private static ResourceReader<List<String>> createSchemaQueryReader;
	private static ResourceReader<List<String>> dropSchemaQueryReader;

	public SchoolApp(MyDataSource dataSource) throws IOException, SQLException {
		this.myDataSource = dataSource;
		SchoolApp.groupsDAO = new GroupsDAOImpl();
		SchoolApp.studentsDAO = new StudentsDAOImpl();
		SchoolApp.coursesDAO = new CoursesDAOImpl();

		groupsService = new GroupsServiceImpl(dataSource, groupsDAO);
		studentsService = new StudentsServiceImpl(dataSource, studentsDAO);
		coursesService = new CoursesServiceImpl(dataSource, coursesDAO);

		createAllTablesQueryReader = new SQLScriptReaderImpl(PATH_CREATE_ALL_TABLES_QUERY);
		dropAllTablesQueryReader = new SQLScriptReaderImpl(PATH_DROP_ALL_TABLES_QUERY);
		createSchemaQueryReader = new SQLScriptReaderImpl(PATH_CREATE_SCHEMA);
		dropSchemaQueryReader = new SQLScriptReaderImpl(PATH_DROP_SCHEMA);

		DDLPerformer schemaCreationCourses = new DDLPerformerImpl(myDataSource, createSchemaQueryReader);
		DDLPerformer tableCreationCourses = new DDLPerformerImpl(myDataSource, createAllTablesQueryReader);
		try {
			schemaCreationCourses.performDDL();
			tableCreationCourses.performDDL();
		} catch (SQLException e) {
			String message = String.format(
					"The error occurred during courses table creation. SQLState %s, Error code %s", e.getSQLState(),
					e.getSQLState());
			throw new SQLException(message, e);
		}

		TestDataGenerator<List<String>> courseGenerator = (TestDataGenerator<List<String>>) TestDataGeneratorsAbstractFactory
				.getFactory("GeneratorFactoryImpl").orElseThrow().getTestDataGenerator("CourseGeneratorImpl")
				.orElseThrow();
		TestDataGenerator<List<String>> groupGenerator = (TestDataGenerator<List<String>>) TestDataGeneratorsAbstractFactory
				.getFactory("GeneratorFactoryImpl").orElseThrow().getTestDataGenerator("GroupGeneratorImpl")
				.orElseThrow();
		TestDataGenerator<List<String>> studentNamesGenerator = (TestDataGenerator<List<String>>) TestDataGeneratorsAbstractFactory
				.getFactory("GeneratorFactoryImpl").orElseThrow().getTestDataGenerator("StudentNamesGeneratorImpl")
				.orElseThrow();
		TestDataGenerator<Map<Integer, ArrayList<Integer>>> studentToCourseAssigner = (TestDataGenerator<Map<Integer, ArrayList<Integer>>>) TestDataGeneratorsAbstractFactory
				.getFactory("AssignerFactoryImpl").orElseThrow().getTestDataGenerator("StudentToCourseAssignerImpl")
				.orElseThrow();
		TestDataGenerator<Map<Integer, ArrayList<Integer>>> studentToGroupAssigner = (TestDataGenerator<Map<Integer, ArrayList<Integer>>>) TestDataGeneratorsAbstractFactory
				.getFactory("AssignerFactoryImpl").orElseThrow().getTestDataGenerator("StudentToGroupAssignerImpl")
				.orElseThrow();

		List<String> coursesList = courseGenerator.returnTestData();
		List<String> groupsList = groupGenerator.returnTestData();
		List<String> studentNamesList = studentNamesGenerator.returnTestData();

		Map<Integer, ArrayList<Integer>> studentToGroupMap = studentToGroupAssigner.returnTestData();
		Map<Integer, ArrayList<Integer>> studentToCourseMap = studentToCourseAssigner.returnTestData();

		TestDataPopulator<List<String>> coursesTestDataPopulatorImpl = new CoursesTestDataPopulatorImpl(myDataSource);
		try {
			coursesTestDataPopulatorImpl.populate(coursesList);
		} catch (SQLException e) {
			String message = String.format(
					"The error occurred during courses table population. SQLState %s, Error code %s", e.getSQLState(),
					e.getErrorCode());
			throw new SQLException(message);
		}
		TestDataPopulator<List<String>> groupsTestDataPopulatorImpl = new GroupsTestDataPopulatorImpl(myDataSource);
		try {
			groupsTestDataPopulatorImpl.populate(groupsList);
		} catch (SQLException e) {
			String message = String.format(
					"The error occurred during groups table population. SQLState %s, Error code %s", e.getSQLState(),
					e.getErrorCode());
			throw new SQLException(message);
		}
		TestDataPopulator<List<String>> studentsDAOTestDataPopulator = new StudentsTestDataPopulatorImpl(myDataSource);
		try {
			studentsDAOTestDataPopulator.populate(studentNamesList);
		} catch (SQLException e) {
			String message = String.format(
					"The error occurred during students table population. SQLState %s, Error code %s", e.getSQLState(),
					e.getErrorCode());
			throw new SQLException(message);
		}
		TestDataPopulator<Map<Integer, ArrayList<Integer>>> studentToGroupAllocator = new StudentToGroupAllocatorImpl(
				myDataSource);
		try {
			studentToGroupAllocator.populate(studentToGroupMap);
		} catch (SQLException e) {
			String message = String.format(
					"The error occurred during student to group allocation. SQLState %s, Error code %s",
					e.getSQLState(), e.getErrorCode());
			throw new SQLException(message);
		}
		TestDataPopulator<Map<Integer, ArrayList<Integer>>> studentToCourseDAOTestDataPopulator = new StudentToCourseTestDataPopulatorImpl(
				myDataSource);
		try {
			studentToCourseDAOTestDataPopulator.populate(studentToCourseMap);
		} catch (SQLException e) {
			String message = String.format(
					"The error occurred during student to courses allocating. SQLState %s, Error code %s",
					e.getSQLState(), e.getErrorCode());
			throw new SQLException(message);
		}
	}

	@Override
	public void run() throws Exception {
		console.show();
	}

	@Override
	public void close() throws IOException {
		DDLPerformer tableDeletor = new DDLPerformerImpl(myDataSource, dropAllTablesQueryReader);
		DDLPerformer schemaDeletor = new DDLPerformerImpl(myDataSource, dropSchemaQueryReader);
		try {
			tableDeletor.performDDL();
			schemaDeletor.performDDL();
		} catch (SQLException e) {
			String message = String.format(
					"The error occurred during courses table creation. SQLState %s, Error code %s", e.getSQLState(),
					e.getSQLState());
			throw new RuntimeException(message);
		}
	}

	public static void main(String[] args) throws Exception {
		final ResourceReader<Properties> databaseAccessPropertiesReader = new PropertiesReaderImpl(
				PATH_DATABASE_ACCESS_INFO);
		final Properties dbProperties = databaseAccessPropertiesReader.read();
		try (MyDataSource datasource = new MyDataSourceImpl(dbProperties);
				SchoolApp app = new SchoolApp(datasource);
				MyConsole console = new MyConsoleImpl(groupsService, studentsService, coursesService)) {
			SchoolApp.console = console;
			app.run();
		}
	}
}
