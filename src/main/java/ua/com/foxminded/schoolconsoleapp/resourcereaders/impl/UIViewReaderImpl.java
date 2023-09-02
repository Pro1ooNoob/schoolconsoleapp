package ua.com.foxminded.schoolconsoleapp.resourcereaders.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

public class UIViewReaderImpl implements ResourceReader<String> {
    private Path pathToViewFormat;
    private Logger log = LoggerFactory.getLogger(getClass());

    public UIViewReaderImpl(Path path) {
        this.pathToViewFormat = path;
    }

    @Override
    public String read() throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToViewFormat.toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } catch (FileNotFoundException e) {
            String message = String.format("Can't find file %s", pathToViewFormat.toString());
            log.error(message);
            throw new FileNotFoundException(message);
        } catch (IOException e) {
            String message = String.format("Can't read from file %s", pathToViewFormat.toString());
            log.error(message);
            throw new IOException(message);
        }
        return new String(builder);
    }
}
