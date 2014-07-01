/*
*Copyright (C) 2013 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
*
*This program is free software: you can redistribute it and/or modify
*it under the terms of the GNU General Public License as published by
*the Free Software Foundation; either version 2 of the License, or
*(at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License along
*with this program; if not, write to the Free Software Foundation, Inc.,
*51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
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
