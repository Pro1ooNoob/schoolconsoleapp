package ua.com.foxminded.schoolconsoleapp.resourcereaders.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

public class SQLScriptReaderImpl implements ResourceReader<List<String>> {
    private Path[] paths;
    private Logger log = LoggerFactory.getLogger(getClass());

    public SQLScriptReaderImpl(Path... paths) {
        this.paths = paths;
    }

    @Override
    public List<String> read() throws IOException {
        List<String> querys = new ArrayList<>();
        for (int i = 0; i < paths.length; i++) {
            StringBuilder query = new StringBuilder();
            try (BufferedReader befferedReader = new BufferedReader(new FileReader(paths[i].toString()))) {
                String line;
                while ((line = befferedReader.readLine()) != null) {
                    line = line.trim();
                    query.append(line);
                    query.append('\n');
                    if (!line.isEmpty() && line.charAt(line.length() - 1) == ';') {
                        String string = new String(query);
                        string = string.replaceAll("\n$", "");
                        querys.add(string);
                        query.setLength(0);
                    }
                }
            } catch (FileNotFoundException e) {
                String message = String.format("Can't find file %s", Arrays.toString(paths));
                log.error(message);
                throw new FileNotFoundException(message);
            } catch (IOException e) {
                String message = String.format("Can't read from file %s", Arrays.toString(paths));
                log.error(message);
                throw new IOException(message);
            }
        }
        return querys;
    }
}
