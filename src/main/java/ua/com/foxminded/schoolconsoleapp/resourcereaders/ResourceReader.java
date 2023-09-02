package ua.com.foxminded.schoolconsoleapp.resourcereaders;

import java.io.IOException;

public interface ResourceReader<T> {
    T read() throws IOException;
}
