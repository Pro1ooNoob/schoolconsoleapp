package ua.com.foxminded.schoolconsoleapp.resourcereaders.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

public class SimpleTxtLinesReaderImpl implements ResourceReader<List<String>> {    
    private Path path;
    private Logger log = LoggerFactory.getLogger(getClass());
    
    public SimpleTxtLinesReaderImpl (Path path) {
        this.path = path;
    }
    
    @Override
    public List<String> read() throws IOException {
        List<String> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                courses.add(line);
            }
        } catch (FileNotFoundException e) {
            String message = String.format("Can't find file %s", path.toString());
            log.error(message);
            throw new FileNotFoundException(message);
        } catch (IOException e) {
            String message = String.format("Can't read from file %s", path.toString());
            log.error(message);
            throw new IOException(message);
        }
        return courses;
    }
}
