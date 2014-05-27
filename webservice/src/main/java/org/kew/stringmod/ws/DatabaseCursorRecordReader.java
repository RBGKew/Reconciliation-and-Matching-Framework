package org.kew.stringmod.ws;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.kew.stringmod.dedupl.DatabaseRecordSource;

/**
 * Reads records from a database using a cursor.
 */
public class DatabaseCursorRecordReader implements DatabaseRecordSource {

	private DataSource dataSource;
	private String sql;
	private int fetchSize = 5000;

	private PreparedStatement preparedStatement;
	private ResultSet rs;

	private void openCursor() throws SQLException {
		preparedStatement = dataSource.getConnection().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		preparedStatement.setFetchSize(fetchSize);
		preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);

		this.rs = preparedStatement.executeQuery();
	}

	/* • Getters and setters  */
	@Override
	public ResultSet getResultSet() throws SQLException {
		if (rs == null) {
			openCursor();
		}

		return rs;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}

	public int getFetchSize() {
		return fetchSize;
	}
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
}
