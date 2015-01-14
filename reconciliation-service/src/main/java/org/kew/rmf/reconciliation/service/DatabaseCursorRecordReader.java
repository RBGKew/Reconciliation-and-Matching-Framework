/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.reconciliation.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.kew.rmf.core.DatabaseRecordSource;

/**
 * Reads records from a database using a cursor.
 */
public class DatabaseCursorRecordReader implements DatabaseRecordSource {
	private Connection connection;
	private DataSource dataSource;
	private String sql;
	private String countSql;
	private int fetchSize = 5000;

	private PreparedStatement preparedStatement;
	private ResultSet rs;

	private void openCursor() throws SQLException {
		preparedStatement = getConnection().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		if (getConnection().getMetaData().getDatabaseProductName().equals("MySQL")) {
			preparedStatement.setFetchSize(Integer.MIN_VALUE);
		}
		else {
			preparedStatement.setFetchSize(fetchSize);
		}
		preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);

		this.rs = preparedStatement.executeQuery();
	}

	@Override
	public int count() throws SQLException {
		if (countSql == null) {
			return -1;
		}
		Statement s = getConnection().createStatement();
		ResultSet r = s.executeQuery(countSql);
		r.next();
		return r.getInt(1);
	}

	/* • Getters and setters  */
	private synchronized Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false); // Cursors in PostgreSQL (at least) don't work in autocommit mode.
		}

		return connection;
	}

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

	public String getCountSql() {
		return countSql;
	}
	public void setCountSql(String countSql) {
		this.countSql = countSql;
	}

	public int getFetchSize() {
		return fetchSize;
	}
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
}
