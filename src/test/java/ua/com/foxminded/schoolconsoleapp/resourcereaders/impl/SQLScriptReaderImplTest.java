package ua.com.foxminded.schoolconsoleapp.resourcereaders.impl;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

class SQLScriptReaderImplTest {
    private final Path pathQueries = Paths.get("src", "test", "resources", "sqlscriptreaderimpltestresources",
            "queries.sql");
    private final Path pathQueries1 = Paths.get("src", "test", "resources", "sqlscriptreaderimpltestresources",
            "queries1.sql");
    private final Path pathEmptyFile = Paths.get("src", "test", "resources", "sqlscriptreaderimpltestresources",
            "emptyfile.sql");

    @ParameterizedTest
    @MethodSource("provideExpectedQueriesListForSinglePath")
    void read_AskToReadAllQueriesFromSinglePath_ShouldReturnExpectedList(List<String> expectedList)
            throws FileNotFoundException, IOException {
        ResourceReader<List<String>> reader = new SQLScriptReaderImpl(pathQueries);
        List<String> actualList = reader.read();
        assertLinesMatch(expectedList, actualList);
    }

    @ParameterizedTest
    @MethodSource("provideExpectedQueriesListForTwoPaths")
    void read_AskToReadAllQueriesFromTwoPaths_ShouldReturnExpectedList(List<String> expectedList)
            throws FileNotFoundException, IOException {
        ResourceReader<List<String>> reader = new SQLScriptReaderImpl(pathQueries, pathQueries1);
        List<String> actualList = reader.read();
        assertLinesMatch(expectedList, actualList);
    }

    @ParameterizedTest
    @MethodSource("provideExpectedQueriesEmptyList")
    void read_AskToReadAllQueriesFromEmptyFile_ShouldReturnEmptyList(List<String> expectedList)
            throws FileNotFoundException, IOException {
        ResourceReader<List<String>> reader = new SQLScriptReaderImpl(pathEmptyFile);
        List<String> actualList = reader.read();
        assertLinesMatch(expectedList, actualList);
    }

    private static Stream<Arguments> provideExpectedQueriesEmptyList() {
        return Stream.of(Arguments.of(Arrays.asList()));
    }

    private static Stream<Arguments> provideExpectedQueriesListForSinglePath() {
        return Stream.of(Arguments.of(Arrays.asList("DROP TABLE IF EXISTS school.courses CASCADE;",
                "CREATE TABLE school.courses (\n" + "course_id SERIAL PRIMARY KEY,\n"
                        + "course_name TEXT UNIQUE NOT NULL,\n" + "course_description TEXT\n" + ");",
                "INSERT INTO school.courses (course_name, course_description) VALUES ('course1', 'info1');",
                "INSERT INTO school.courses (course_name, course_description) VALUES ('course2', 'info2');",
                "INSERT INTO school.courses (course_name, course_description) VALUES ('course3', 'info3');",
                "INSERT INTO school.courses (course_name, course_description) VALUES ('course4', 'info4');")));
    }

    private static Stream<Arguments> provideExpectedQueriesListForTwoPaths() {
        return Stream.of(Arguments.of(Arrays.asList("DROP TABLE IF EXISTS school.courses CASCADE;",
                "CREATE TABLE school.courses (\n" + "course_id SERIAL PRIMARY KEY,\n"
                        + "course_name TEXT UNIQUE NOT NULL,\n" + "course_description TEXT\n" + ");",
                "INSERT INTO school.courses (course_name, course_description) VALUES ('course1', 'info1');",
                "INSERT INTO school.courses (course_name, course_description) VALUES ('course2', 'info2');",
                "INSERT INTO school.courses (course_name, course_description) VALUES ('course3', 'info3');",
                "INSERT INTO school.courses (course_name, course_description) VALUES ('course4', 'info4');",
                "DROP TABLE IF EXISTS school.student_to_course CASCADE;",
                "CREATE TABLE school.student_to_course (\n"
                        + "student_id INTEGER REFERENCES school.students (student_id) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                        + "course_id INTEGER REFERENCES school.courses (course_id) ON DELETE CASCADE ON UPDATE CASCADE\n"
                        + ");")));
    }
}
