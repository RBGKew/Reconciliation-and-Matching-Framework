package org.kew.rmf.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Reads records from a database.
 */
public interface DatabaseRecordSource {
	public ResultSet getResultSet() throws SQLException;
	public void close() throws SQLException;
}
