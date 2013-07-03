/*******************************************************************************
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
******************************************************************************/
package au.com.redboxresearchdata.harvester.csvjdbc

import au.com.redboxresearchdata.util.integration.interceptor.TransmissionInterceptorAdapter
import java.io.File
import java.io.Reader
import java.sql.Connection
import java.util.Date
import java.sql.SQLException
import java.sql.Statement
import java.util.HashMap
import java.util.List
import java.util.concurrent.ArrayBlockingQueue

import org.apache.log4j.Logger;
import org.relique.io.TableReader
import org.springframework.integration.annotation.Header
import org.springframework.integration.annotation.Payload
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.TriggerContext
import org.springframework.integration.Message
import org.springframework.integration.support.MessageBuilder
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
/**
 * Bridges Spring Integration's file and jdbc mechanisms for CSVJDBC driver.
 *  
 * 
 * @author Shilo Banihit
 * @since 1.0
 */
class CsvJdbcBridge extends AbstractSqlParameterSource implements TableReader, Trigger {

	private static final Logger log = Logger.getLogger(CsvJdbcBridge.class)
	
	Expando currentEntry
	ArrayBlockingQueue<Expando> queue
	TransmissionInterceptorAdapter channelInterceptor
	ConfigObject config
	
	public CsvJdbcBridge(String queueCapacity) {
		log.info("Creating queue with capacity:${queueCapacity}")
		queue = new ArrayBlockingQueue<Expando>(Integer.parseInt(queueCapacity))
		CsvFileReader._readerDelegate = this
	}
	
	public void addTable(@Header("type") String tableName, @Payload File file) {
		def entry = new Expando()
		entry.table = tableName
		entry.file = file
		queue.put(entry)
		channelInterceptor.incMessageCount()
		synchronized(this) {
			this.notifyAll()
		}
	}
	
	public String getType(payload) {
		return currentEntry.table
	}
	
	public File getOriginalFile(payload) {
		return currentEntry.file
	}
	
	public Message<?> moveSourceFile(Message<?> message, @Header("original_file") file, @Header("type") table) {
		if (file == null) {
			log.error("Tried to move source file with no valid file reference.")
			return
		}
		def newName = config.harvest.output.directory + table + "_" + new Date().format(config.harvest.output.dateFormat) + ".csv"
		if (file.renameTo(new File(newName))) {
			if (log.isDebugEnabled()) {
				log.debug("File '${table}.csv' renamed to '${newName}'")
			}
		} else {
			log.error("Failed to rename '${table}.csv' to '${newName}'")
		}
		return message
	}
	
	@Override
	public Reader getReader(Statement statement, String tableName) throws SQLException {
		def entry = queue.take()
		currentEntry = entry
		return new FileReader(entry.file)
	}

	@Override
	public List<String> getTableNames(Connection conn) throws SQLException {
		return new ArrayList<String>()
	}

	@Override
	public Date nextExecutionTime(TriggerContext arg0) {
		synchronized(this) {
			while(queue.size() == 0) {
				this.wait()
			}
		}
		return new Date(System.currentTimeMillis())
	}

	@Override
	public Object getValue(String param) throws IllegalArgumentException {
		if (param.equals("table")) {
			return currentTable
		}
		return currentTable
	}

	@Override
	public boolean hasValue(String param) {
		if (param.equalsIgnoreCase("table")) {
			return true
		}
		return true
	}

}
