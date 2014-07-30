package org.kew.reconciliation.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.kew.stringmod.dedupl.DatabaseRecordSource;

/**
 * Reads records from a database using a cursor.
 */
public class DatabaseCursorRecordReader implements DatabaseRecordSource {
	private Connection connection;
	private DataSource dataSource;
	private String sql;
	private int fetchSize = 5000;

	private PreparedStatement preparedStatement;
	private ResultSet rs;

	private void openCursor() throws SQLException {
		connection = dataSource.getConnection();
		connection.setAutoCommit(false); // Cursors in PostgreSQL (at least) don't work in autocommit mode.
		preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		if (dataSource.getConnection().getMetaData().getDatabaseProductName().equals("MySQL")) {
			preparedStatement.setFetchSize(Integer.MIN_VALUE);
		}
		else {
			preparedStatement.setFetchSize(fetchSize);
		}
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

	@Override
	public void close() throws SQLException {
		if (rs != null) {
			rs.close();
		}
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (connection != null) {
			connection.close();
		}
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
