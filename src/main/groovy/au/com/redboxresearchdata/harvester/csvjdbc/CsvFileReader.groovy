package au.com.redboxresearchdata.harvester.csvjdbc

import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.relique.io.TableReader;

/**
 * Reader class for CSVJDBC Driver. Delegates all calls to CsvJdbcBridge.
 * 
 * @author Shilo Banihit
 * @since 1.0
 */
class CsvFileReader implements TableReader{

	static CsvJdbcBridge _readerDelegate
	
	@Override
	public Reader getReader(Statement arg0, String arg1) throws SQLException {
		return _readerDelegate.getReader(arg0, arg1)
	}

	@Override
	public List<String> getTableNames(Connection arg0) throws SQLException {
		return _readerDelegate.getTableNames(arg0)
	}
}
