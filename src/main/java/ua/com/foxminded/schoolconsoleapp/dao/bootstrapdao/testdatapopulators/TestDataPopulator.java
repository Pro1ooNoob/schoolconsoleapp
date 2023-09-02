package ua.com.foxminded.schoolconsoleapp.dao.bootstrapdao.testdatapopulators;

import java.io.IOException;
import java.sql.SQLException;

public interface TestDataPopulator<T> {
    boolean populate(T testDataList) throws SQLException, IOException;
}
