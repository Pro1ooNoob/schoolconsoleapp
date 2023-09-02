package ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer;

import java.io.IOException;
import java.sql.SQLException;

public interface DDLPerformer {
	boolean performDDL() throws SQLException, IOException;
}
