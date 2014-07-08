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

import au.com.redboxresearchdata.util.integration.interceptor.TransmissionInterceptorAdapter

import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import java.util.concurrent.ArrayBlockingQueue

import org.apache.log4j.Logger;
import org.relique.io.TableReader
import org.springframework.integration.annotation.Header
import org.springframework.integration.annotation.Payload
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.TriggerContext
import org.springframework.integration.Message
import org.springframework.integration.context.OrderlyShutdownCapable

/**
 * Bridges Spring Integration's file and JDBC mechanisms for CSVJDBC's file-specific awareness.
 * 
 * The assumption is that the instance is used in linear fashion, as in a chain.
 * 
 * @author <a href="https://github.com/shilob" target="_blank">Shilo Banihit</a>
 * @since 1.0
 */
class CsvJdbcBridge implements TableReader, Trigger, OrderlyShutdownCapable {

	private static final Logger log = Logger.getLogger(CsvJdbcBridge.class)
	/**
	 * The current entry reference.
	 */
	Expando currentEntry
	/**
	 * Blocking queue of entries.
	 */
	ArrayBlockingQueue<Expando> queue
	/**
	 * The interceptor reference
	 */
	TransmissionInterceptorAdapter channelInterceptor
	/**
	 * The config object
	 */
	ConfigObject config
	
	boolean shuttingDown = false
	
	/**
	 * Creates a bridge with the initial capacity.
	 * 
	 * @param queueCapacity
	 */
	CsvJdbcBridge(String queueCapacity) {
		log.info("Creating queue with capacity:${queueCapacity}")
		queue = new ArrayBlockingQueue<Expando>(Integer.parseInt(queueCapacity))
		CsvFileReader._readerDelegate = this
	}
	
	/**
	 * Used to add an entry to the queue.
	 * 
	 * @param tableName
	 * @param file
	 */
	void addTable(@Header("type") String tableName, @Payload File file) {
		def entry = new Expando()
		entry.table = tableName
		entry.file = file
		queue.put(entry)
		channelInterceptor.incMessageCount()
		synchronized(this) {
			this.notifyAll()
		}
	}
	
	/**
	 * Used to get the table name of the current entry.
	 * 
	 * @param payload
	 * @return
	 */
	String getTable(payload) {
		return currentEntry.table
	}
	
	/**
	 * Gets the File reference of the current entry.
	 *   
	 * @param payload
	 * @return
	 */
	File getOriginalFile(payload) {
		return currentEntry.file
	}
	/**
	 * Moves/renames the source file, and returns the original message.
	 * 
	 * Can be used by service activators in a chain.
	 * 
	 * @param message
	 * @param file
	 * @param table
	 * @return
	 */
	Message<?> moveSourceFile(Message<?> message, @Header("original_file") file, @Header("type") table) {
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

    /**
     * Moves/renames the source file, under 'harvestType' sub-folder, and returns the original message.
     *
     * Can be used by service activators in a chain.
     *
     * @param message
     * @param originalFile
     * @param harvestType - used for destination sub-folder
     * @param table  - used as destination file name
     * @return message
     */
    Message<?> moveSourceFileUsingHarvestType(Message<?> message, @Header("original_file")File originalFile, @Header("harvestType") harvestType, @Header("type") table) {
        if (originalFile == null) {
            log.error("Tried to move source file with no valid file reference.")
            return
        }

        File destinationParent = createParentDirectory(config.harvest.output.directory + harvestType)

        String destinationChild = table + "_" + new Date().format(config.harvest.output.dateFormat) + ".csv"
        File destinationFile = new File(destinationParent, destinationChild)

        if (originalFile.renameTo(destinationFile)) {
            if (log.isDebugEnabled()) {
                log.debug("File '${originalFile}' renamed to '${destinationFile}'")
            }
        } else {
            log.error("Failed to rename '${originalFile}' to '${destinationParent}/${destinationChild}'")
        }
        return message
    }

    private File createParentDirectory(String directoryName) {
        File directory = new File(directoryName)
        // do not create non-existent parent directories
        directory.mkdirs()
        if (log.isDebugEnabled()) {
            log.debug("directory created: '${directory}'.")
        } else {
            log.error("unable to create directory: '${directoryName}'.")
        }
        return directory
    }
	
	/**
	 * Method called by CSVJDBC driver when executing a SQL statement.
	 * 
	 * Pops the head of the queue and sets it as the current entry being processed. 
	 */
	@Override
	Reader getReader(Statement statement, String tableName) throws SQLException {
		def entry = queue.take()
		currentEntry = entry
		return new FileReader(entry.file)
	}

	/**
	 * Method called by CSVJDBC driver. Returns empty list.
	 * 
	 */
	@Override
	List<String> getTableNames(Connection conn) throws SQLException {
		return new ArrayList<String>()
	}

	/**
	 * Called by the poller to determine the next execution time. 
	 * 
	 * Implementation blocks the poller until an entry is pushed to the queue.
	 * 
	 */
	@Override
	Date nextExecutionTime(TriggerContext arg0) {
		log.debug "Called nextExecutionTime..."
		synchronized(this) {
			while(queue.size() == 0 && !shuttingDown) {
				log.debug "Called nextExecutionTime...waiting."
				this.wait()
				log.debug "Called nextExecutionTime...notified."
			}			
			log.info "Called nextExecutionTime...shuttingDown:${shuttingDown}"
			return shuttingDown ? null : new Date(System.currentTimeMillis())
		}
	}
	
	@Override
	public int beforeShutdown() {
		log.info "Orderly shutdown command received - beforeShutdown"
		synchronized(this) {
			shuttingDown = true
			this.notifyAll()
		}
		return channelInterceptor.sendCount.intValue()
	}
	
	@Override
	public int afterShutdown() {
		log.info "Orderly shutdown command received - afterShutdown"
		synchronized(this) {
			shuttingDown = true
			this.notifyAll()
		}
		return channelInterceptor.sendCount.intValue()
	}
}
