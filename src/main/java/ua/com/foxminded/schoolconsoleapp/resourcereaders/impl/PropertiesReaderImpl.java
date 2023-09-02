package ua.com.foxminded.schoolconsoleapp.resourcereaders.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

public class PropertiesReaderImpl implements ResourceReader<Properties> {
    private Path path;
    private Logger log = LoggerFactory.getLogger(getClass());

    public PropertiesReaderImpl(Path path) {
        this.path = path;
    }

    @Override
    public Properties read() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(path.toString())) {
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            String message = String.format("Can't find file %s", path.toString());
            log.error(message);
            throw new FileNotFoundException(message);
        } catch (IOException e) {
            String message = String.format("Can't read from file %s", path.toString());
            log.error(message);
            throw new IOException(message);
        }
        return properties;
    }
}
