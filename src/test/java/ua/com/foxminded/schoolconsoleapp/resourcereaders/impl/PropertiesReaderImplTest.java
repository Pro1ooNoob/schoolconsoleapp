package ua.com.foxminded.schoolconsoleapp.resourcereaders.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

class PropertiesReaderImplTest {

    @Test
    void testReturnPropertiesWhenUsingReadMethod() throws FileNotFoundException, IOException {
        Path path = Paths.get("src", "test", "resources", "config", "databaseaccessinfo.properties");
        ResourceReader<Properties> propertiesReader = new PropertiesReaderImpl(path);
        Properties properties = propertiesReader.read();
        assertAll(
                () -> assertEquals("jdbc:postgresql://localhost:5432/database", properties.getProperty("db.url"),
                        "should be valid url"),
                () -> assertEquals("newuser", properties.getProperty("db.username"), "should be valid user"),
                () -> assertEquals("1234", properties.getProperty("db.password"), "should be valid password"));
    }
}
