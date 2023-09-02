package ua.com.foxminded.schoolconsoleapp.app.menuservice.impl;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import ua.com.foxminded.schoolconsoleapp.app.menuservice.MyConsole;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;
import ua.com.foxminded.schoolconsoleapp.enumerations.optionselection.OptionSelection;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.InputValidator;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.impl.CourseNameValidationImpl;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.impl.IntInputValidationImpl;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.impl.OptionSelectionValidationImpl;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.impl.StudentNameValidationImpl;
import ua.com.foxminded.schoolconsoleapp.inputvalidator.validationexception.ValidationException;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.SimpleTxtLinesReaderImpl;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.impl.UIViewReaderImpl;
import ua.com.foxminded.schoolconsoleapp.services.coursesservice.CoursesService;
import ua.com.foxminded.schoolconsoleapp.services.groupsservice.GroupsService;
import ua.com.foxminded.schoolconsoleapp.services.studentsservice.StudentsService;
import ua.com.foxminded.schoolconsoleapp.ui.outputui.OutputUI;
import ua.com.foxminded.schoolconsoleapp.ui.outputui.impl.OutputGroupsUIImpl;
import ua.com.foxminded.schoolconsoleapp.ui.outputui.impl.OutputStudentsUIImpl;
import ua.com.foxminded.schoolconsoleapp.ui.uicomposer.UIComposer;
import ua.com.foxminded.schoolconsoleapp.ui.uicomposer.impl.UIComposerImpl;

public class MyConsoleImpl implements MyConsole, Closeable {
	private GroupsService groupsService;
	private StudentsService studentsService;
	private CoursesService coursesService;
	private Scanner scanner = new Scanner(System.in);

	public MyConsoleImpl(GroupsService groupsService, StudentsService studentsService, CoursesService coursesService) {
		this.groupsService = groupsService;
		this.studentsService = studentsService;
		this.coursesService = coursesService;
	}

	private static final Path PATH_SHOW_CHOICE_WINDOW = Paths.get("src", "main", "resources",
			"selectoptionuiimplresources", "showchoicewindow", "showchoicewindow.txt");
	private static final Path PATH_ASK_USER_CHOICE_WINDOW = Paths.get("src", "main", "resources",
			"selectoptionuiimplresources", "askuserchoicewindow", "askuserchoicewindow.txt");
	private static final Path PATH_ASK_COURSE_NAME_TXT = Paths.get("src", "main", "resources",
			"selectoptionuiimplresources", "consoleoutput", "askcoursename.txt");
	private static final Path PATH_ASK_FULL_NAME_TXT = Paths.get("src", "main", "resources",
			"selectoptionuiimplresources", "consoleoutput", "askfullname.txt");
	private static final Path PATH_ASK_STUDENT_ID_TXT = Paths.get("src", "main", "resources",
			"selectoptionuiimplresources", "consoleoutput", "askstudentid.txt");
	private static final Path PATH_ASK_USER_AMOUNT_TXT = Paths.get("src", "main", "resources",
			"selectoptionuiimplresources", "consoleoutput", "askuseramount.txt");
	private static final Path PATH_COURSE_NAMES = Paths.get("src", "main", "resources", "initialdata",
			"coursenames.txt");

	final ResourceReader<List<String>> courseNamesReader = new SimpleTxtLinesReaderImpl(PATH_COURSE_NAMES);
	final ResourceReader<String> showChoiceWindowReader = new UIViewReaderImpl(PATH_SHOW_CHOICE_WINDOW);
	final ResourceReader<String> askUserChoiceWindowReader = new UIViewReaderImpl(PATH_ASK_USER_CHOICE_WINDOW);
	final ResourceReader<String> askCourseNameReader = new UIViewReaderImpl(PATH_ASK_COURSE_NAME_TXT);
	final ResourceReader<String> askFullNameReader = new UIViewReaderImpl(PATH_ASK_FULL_NAME_TXT);
	final ResourceReader<String> askStudentIdReader = new UIViewReaderImpl(PATH_ASK_STUDENT_ID_TXT);
	final ResourceReader<String> askUserAmountReader = new UIViewReaderImpl(PATH_ASK_USER_AMOUNT_TXT);

	String showChoiceWindow;
	String askUserChoiceWindow;
	String askCourseName;
	String askFullName;
	String askStudentId;
	String askUserAmount;

	InputValidator<String> optionSelectionValidator = new OptionSelectionValidationImpl();
	InputValidator<String> intInputValidator = new IntInputValidationImpl();
	InputValidator<String> courseNameValidator = new CourseNameValidationImpl(courseNamesReader);
	InputValidator<String> studentNameValidator = new StudentNameValidationImpl();

	OutputUI<GroupsDTO> groupDAOOutputUI = new OutputGroupsUIImpl<>();
	OutputUI<StudentsDTO> studentDTOOutputUI = new OutputStudentsUIImpl<>();

	@Override
	public void show() throws Exception {
		readResources();
		String userSelectionStr;
		OptionSelection userSelectionEnum;
		do {
			{
				String errorMessage;
				UIComposer uiComposer = new UIComposerImpl();
				uiComposer.setMainSelectionWindow(showChoiceWindow);
				uiComposer.setConsoleOutput(askUserChoiceWindow);
				while (true) {
					String view = uiComposer.getComposedInterface();
					System.out.print(view);
					userSelectionStr = scanner.nextLine().toUpperCase();
					try {
						optionSelectionValidator.validate(userSelectionStr);
					} catch (IOException e) {
						throw new IOException(e.getMessage() + e);
					} catch (ValidationException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					}
					userSelectionEnum = OptionSelection.valueOf(userSelectionStr);
					break;
				}
			}
			if (OptionSelection.A == userSelectionEnum) {
				String errorMessage;
				UIComposer uiComposer = new UIComposerImpl();
				uiComposer.setMainSelectionWindow(showChoiceWindow);
				uiComposer.setConsoleOutput(askUserChoiceWindow, askUserAmount);
				uiComposer.setUserArguments(userSelectionStr);
				while (true) {
					try {
						clearConsole();
					} catch (Exception e) {
						throw new Exception(e.getMessage() + e);
					}
					String view = uiComposer.getComposedInterface();
					System.out.print(view);
					String studentAmount = scanner.nextLine();
					try {
						intInputValidator.validate(studentAmount);
					} catch (IOException e) {
						throw new IOException(e.getMessage() + e);
					} catch (ValidationException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					}
					Long studentAmountLong = Long.valueOf(studentAmount);
					List<GroupsDTO> groupsDTOList = groupsService.findAllWithLessOrEqualStudents(studentAmountLong)
							.orElse(new ArrayList<>());
					String groupsDTOListOutput = groupDAOOutputUI.returnView(groupsDTOList);
					System.out.println(groupsDTOListOutput);
					break;
				}
			}
			if (OptionSelection.B == userSelectionEnum) {
				String errorMessage;
				UIComposer uiComposer = new UIComposerImpl();
				uiComposer.setMainSelectionWindow(showChoiceWindow);
				uiComposer.setConsoleOutput(askUserChoiceWindow, askCourseName);
				uiComposer.setUserArguments(userSelectionStr);
				while (true) {
					try {
						clearConsole();
					} catch (Exception e) {
						throw new Exception(e.getMessage() + e);
					}
					String view = uiComposer.getComposedInterface();
					System.out.print(view);
					String courseName = scanner.nextLine();
					try {
						courseNameValidator.validate(courseName);
					} catch (IOException e) {
						throw new IOException(e.getMessage() + e);
					} catch (ValidationException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					}
					List<StudentsDTO> studentsDTOList = new ArrayList<>();
					try {
						studentsDTOList = studentsService.findAllByCourseName(courseName).orElse(new ArrayList<>());
					} catch (DAOException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					} catch (SQLException e) {
						throw new SQLException(e.getMessage() + " " + e);
					}
					String studentsDTOListOutput = studentDTOOutputUI.returnView(studentsDTOList);
					System.out.println(studentsDTOListOutput);
					break;
				}
			}
			if (OptionSelection.C == userSelectionEnum) {
				String errorMessage;
				UIComposer uiComposer = new UIComposerImpl();
				uiComposer.setMainSelectionWindow(showChoiceWindow);
				uiComposer.setConsoleOutput(askUserChoiceWindow, askFullName);
				uiComposer.setUserArguments(userSelectionStr);
				while (true) {
					try {
						clearConsole();
					} catch (Exception e) {
						throw new Exception(e.getMessage() + e);
					}
					String view = uiComposer.getComposedInterface();
					System.out.print(view);
					String studentFullName = scanner.nextLine();
					try {
						studentNameValidator.validate(studentFullName);
					} catch (IOException e) {
						throw new IOException(e.getMessage() + e);
					} catch (ValidationException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					}
					String firstName = studentFullName.split(" ")[0];
					String lastName = studentFullName.split(" ")[1];
					try {
						studentsService.addStudent(firstName, lastName);
					} catch (DAOException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					} catch (SQLException e) {
						throw new IOException(e.getMessage() + e);
					}
					break;
				}
				System.out.println("The student addition succeed");
			}
			if (OptionSelection.D == userSelectionEnum) {
				String errorMessage;
				UIComposer uiComposer = new UIComposerImpl();
				uiComposer.setMainSelectionWindow(showChoiceWindow);
				uiComposer.setConsoleOutput(askUserChoiceWindow, askStudentId);
				uiComposer.setUserArguments(userSelectionStr);
				while (true) {
					try {
						clearConsole();
					} catch (Exception e) {
						throw new Exception(e.getMessage() + e);
					}
					String view = uiComposer.getComposedInterface();
					System.out.print(view);
					String studentIdStr = scanner.nextLine();
					try {
						intInputValidator.validate(studentIdStr);
					} catch (IOException e) {
						throw new IOException(e.getMessage() + e);
					} catch (ValidationException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					}
					Long studentIdLong = Long.valueOf(studentIdStr);
					try {
						studentsService.deleteById(studentIdLong);
					} catch (DAOException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					} catch (SQLException e) {
						throw new SQLException(e.getMessage() + e);
					}
					break;
				}
				System.out.println("The student deletion succeeded");
			}
			if (OptionSelection.E == userSelectionEnum) {
				String errorMessage;
				UIComposer uiComposer = new UIComposerImpl();
				uiComposer.setMainSelectionWindow(showChoiceWindow);
				uiComposer.setConsoleOutput(askUserChoiceWindow, askStudentId);
				uiComposer.setUserArguments(userSelectionStr);
				while (true) {
					try {
						clearConsole();
					} catch (Exception e) {
						throw new Exception(e.getMessage() + e);
					}
					String view = uiComposer.getComposedInterface();
					System.out.print(view);
					String studentId = scanner.nextLine();
					try {
						intInputValidator.validate(studentId);
					} catch (IOException e) {
						throw new IOException(e.getMessage() + e);
					} catch (ValidationException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					}
					UIComposer uiComposer1 = new UIComposerImpl();
					uiComposer1.setMainSelectionWindow(showChoiceWindow);
					uiComposer1.setConsoleOutput(askUserChoiceWindow, askStudentId, askCourseName);
					uiComposer1.setUserArguments(userSelectionStr, studentId);
					String courseName;
					while (true) {
						try {
							clearConsole();
						} catch (Exception e) {
							throw new Exception(e.getMessage() + e);
						}
						String view1 = uiComposer1.getComposedInterface();
						System.out.print(view1);
						courseName = scanner.nextLine();
						try {
							courseNameValidator.validate(courseName);
						} catch (IOException e) {
							throw new IOException(e.getMessage() + e);
						} catch (ValidationException e) {
							errorMessage = e.getMessage();
							uiComposer1.setErrorMessage(errorMessage);
							continue;
						}
						break;
					}
					Long studentIdLong = Long.valueOf(studentId);
					try {
						coursesService.addStudentById(studentIdLong, courseName);
					} catch (DAOException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					} catch (SQLException e) {
						throw new SQLException(e.getMessage() + e);
					}
					break;
				}
				System.out.println("The addition succeed");
			}
			if (OptionSelection.F == userSelectionEnum) {
				String errorMessage;
				UIComposer uiComposer = new UIComposerImpl();
				uiComposer.setMainSelectionWindow(showChoiceWindow);
				uiComposer.setConsoleOutput(askUserChoiceWindow, askStudentId);
				uiComposer.setUserArguments(userSelectionStr);
				while (true) {
					try {
						clearConsole();
					} catch (Exception e) {
						throw new Exception(e.getMessage() + e);
					}
					String view = uiComposer.getComposedInterface();
					System.out.print(view);
					String studentId = scanner.nextLine();
					try {
						intInputValidator.validate(studentId);
					} catch (IOException e) {
						throw new IOException(e.getMessage() + e);
					} catch (ValidationException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					}
					UIComposer uiComposer1 = new UIComposerImpl();
					uiComposer1.setMainSelectionWindow(showChoiceWindow);
					uiComposer1.setConsoleOutput(askUserChoiceWindow, askStudentId, askCourseName);
					uiComposer1.setUserArguments(userSelectionStr, studentId);
					String courseName;
					while (true) {
						try {
							clearConsole();
						} catch (Exception e) {
							throw new Exception(e.getMessage() + e);
						}
						String view1 = uiComposer1.getComposedInterface();
						System.out.print(view1);
						courseName = scanner.nextLine();
						try {
							courseNameValidator.validate(courseName);
						} catch (IOException e) {
							throw new IOException(e.getMessage() + e);
						} catch (ValidationException e) {
							errorMessage = e.getMessage();
							uiComposer1.setErrorMessage(errorMessage);
							continue;
						}
						break;
					}
					Long studentIdLong = Long.valueOf(studentId);
					try {
						coursesService.removeStudentById(studentIdLong, courseName);
					} catch (DAOException e) {
						errorMessage = e.getMessage();
						uiComposer.setErrorMessage(errorMessage);
						continue;
					} catch (SQLException e) {
						throw new IOException(e.getMessage() + e);
					}
					break;
				}
				System.out.println("The removing succeed");
			}

		} while (OptionSelection.G != userSelectionEnum);
	}

	public void readResources() throws IOException {
		showChoiceWindow = showChoiceWindowReader.read();
		askUserChoiceWindow = askUserChoiceWindowReader.read();
		askCourseName = askCourseNameReader.read();
		askFullName = askFullNameReader.read();
		askStudentId = askStudentIdReader.read();
		askUserAmount = askUserAmountReader.read();
	}

	public void clearConsole() throws Exception {
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				new ProcessBuilder("clear").inheritIO().start().waitFor();
			}
		} catch (Exception e) {
			throw new Exception("An error occurred while clearing console:", e);
		}
	}

	@Override
	public void close() throws IOException {
		scanner.close();
	}
}
