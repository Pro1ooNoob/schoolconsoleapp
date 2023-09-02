package ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.com.foxminded.schoolconsoleapp.bootstrap.ddlperformer.DDLPerformer;
import ua.com.foxminded.schoolconsoleapp.databaseconnection.mydatasource.MyDataSource;
import ua.com.foxminded.schoolconsoleapp.resourcereaders.ResourceReader;

public class DDLPerformerImpl implements DDLPerformer {
	private Logger log = LoggerFactory.getLogger(getClass());
	private MyDataSource myDataSource;
	private ResourceReader<List<String>> ddlReader;

	public DDLPerformerImpl(MyDataSource myDataSource, ResourceReader<List<String>> ddlReader) {
		this.myDataSource = myDataSource;
		this.ddlReader = ddlReader;
	}

	@Override
	public boolean performDDL() throws SQLException, IOException {
		List<String> ddlQuery = new ArrayList<>();
		try {
			if (ddlReader != null) {
				ddlQuery = ddlReader.read();
			}

		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(e.getMessage() + e);
		} catch (IOException e) {
			throw new IOException(e.getMessage() + e);
		}
		int[] affectedRowsArray = null;

		try (Connection connection = myDataSource.getConnection(); Statement statement = connection.createStatement()) {
			for (int i = 0; i < ddlQuery.size(); i++) {
				statement.addBatch(ddlQuery.get(i));
			}
			affectedRowsArray = statement.executeBatch();
		} catch (SQLException e) {
			String query = ddlQuery.stream().collect(Collectors.joining("\n"));
			String message = String.format(
					"The query%n%s%n-----> %s <------ %nAn exception occurred while trying to compose query or execute batch. SQLState: %s Error Code: %s",
					query, e.getMessage(), e.getSQLState(), e.getErrorCode());
			log.error(message);
			throw new SQLException(message, e);
		}
		return Stream.of(Optional.ofNullable(affectedRowsArray).orElse(new int[] { 0 })).count() > 0;
	}
}
