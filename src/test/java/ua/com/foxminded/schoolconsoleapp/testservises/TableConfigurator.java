package ua.com.foxminded.schoolconsoleapp.testservises;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.List;

public interface TableConfigurator extends Closeable{
    void configure(final List<String> configureScript) throws SQLException;
}
