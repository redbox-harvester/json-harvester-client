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
package au.com.redboxresearchdata.harvester.json.transformer

import java.io.File
import java.io.IOException
import java.util.List
import java.util.Map

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.log4j.Logger
import org.springframework.integration.Message
import org.springframework.integration.annotation.Header
import org.springframework.integration.annotation.Payload
import org.springframework.integration.annotation.Transformer
import org.springframework.integration.file.FileHeaders
import org.springframework.integration.support.MessageBuilder

import au.com.redboxresearchdata.json.JsonFactory

/**
 * 
 * Transformer for creating JSON strings using the JsonFactory class.
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class JsonTransformationHandler {

	private static final Logger log = Logger.getLogger(JsonTransformationHandler.class)
	ConfigObject config
	
	/**
	 * Default file handler: loads the contents of the file as message payload.
	 * 
	 * Sets the filename, file size, file extension, filename without extension as headers.
	 * It also sets the FileHeaders.ORIGINAL_FILE File reference.
	 * 
	 * @param inputMessage
	 * @return SI Message with String payload.
	 */
	@Transformer
	Message<String> handleFile(final Message<File> inputMessage) {

		final File inputFile = inputMessage.getPayload()
		final String filename = inputFile.getName()
		final String fileExtension = FilenameUtils.getExtension(filename)

		final String inputAsString

		try {
			inputAsString = FileUtils.readFileToString(inputFile)
		} catch (IOException e) {
			throw new IllegalStateException(e)
		}

		final Message<String> message = MessageBuilder.withPayload(inputAsString)
					.setHeader(FileHeaders.FILENAME,      filename)
					.setHeader(FileHeaders.ORIGINAL_FILE, inputFile)
					.setHeader("file_size", inputFile.length())
					.setHeader("file_extension", fileExtension)
					.setHeader("file_name_without_ext", FilenameUtils.removeExtension(filename))
					.build()

		return message
	}
	
	/**
	 *  Sets the headers specified at {@link #handleFile(Message<File>)}, but places the File reference to the payload.
	 * 
	 *  Also sets a "type" header: the file name less the extension.
	 *  
	 * @param inputMessage
	 * @return
	 */
	@Transformer
	Message<File> handleFileHeaders(final Message<File> inputMessage) {
		final File inputFile = inputMessage.getPayload()
		final String filename = inputFile.getName()
		final String filenameWithoutExt = FilenameUtils.removeExtension(filename)
		final String fileExtension = FilenameUtils.getExtension(filename)

		final Message<String> message = MessageBuilder.withPayload(inputFile)
					.setHeader(FileHeaders.FILENAME,      filename)
					.setHeader(FileHeaders.ORIGINAL_FILE, inputFile)
					.setHeader("file_size", inputFile.length())
					.setHeader("file_extension", fileExtension)
					.setHeader("file_name_without_ext", filenameWithoutExt)
					.setHeader("type", filenameWithoutExt)
					.build()

		return message
	}
		
	/**
	 * Used to transform a List of Maps or a JDBC-like result set to a JSON harvest request message. 
	 * 
	 * Requires a "type" header that specifies the data type of the result set.
	 *  
	 * @param payload
	 * @param type
	 * @return JSON harvest request message.
	 */
	@Transformer
	Message<String> handleJdbc(@Payload List<Map> payload, @Header("type") String type) {
		String data = JsonFactory.buildJsonStr(payload, type, config)
		if (log.isDebugEnabled()) {
			log.debug("JSON message payload: ${data}")
		}
		final Message<String> message = MessageBuilder.withPayload(data)
				.setHeader("type", type)
				.build()

		return message
	}

	/**
	 * Used to transform an entire CSVJDBC resultset to a JSON harvest request message.
	 * 
	 * Please see http://csvjdbc.sourceforge.net/ for details about CSVJDBC
	 * 
	 * @param payload
	 * @param type - a "type" header
	 * @param origFile - a "original_file" header, specifying the csv File reference or path
	 * @return  JSON harvest request message.
	 */
	@Transformer
	Message<String> handleCsvJdbc(@Payload List<Map> payload, @Header("type") String type, @Header("original_file") origFile) {
		String data = JsonFactory.buildJsonStr(payload, type, config)
		if (log.isDebugEnabled()) {
			log.debug("JSON message payload: ${data}")
		}
		final Message<String> message = MessageBuilder.withPayload(data)
				.setHeader("type", type)
				.setHeader("original_file", origFile)
				.setHeader(FileHeaders.ORIGINAL_FILE, origFile)
				.build()

		return message
	}

    /**
     * Used to transform an entire CSVJDBC resultset to a JSON harvest request message.
     *
     * Please see http://csvjdbc.sourceforge.net/ for details about CSVJDBC
     *
     * @param payload
     * @param type - a "type" header, matching record table
     * @param origFile - a "original_file" header, specifying the csv File reference or path
     * @param  harvestType - overall harvestType
     * @return  JSON harvest request message.
     */
    @Transformer
    Message<String> handleCsvJdbcUsingHarvestType(@Payload List<Map> payload, @Header("type") String type, @Header("original_file") origFile, @Header("harvestType") harvestType) {
        String data = JsonFactory.buildJsonStr(payload, type, config, harvestType)
        if (log.isDebugEnabled()) {
            log.debug("JSON message payload: ${data}")
        }
        final Message<String> message = MessageBuilder.withPayload(data)
                .setHeader("harvestType", harvestType)
                .setHeader("original_file", origFile)
                .setHeader(FileHeaders.ORIGINAL_FILE, origFile)
                .build()
        return message
    }
	
	@Transformer
	Message<String> handleRecord(@Payload Map payload, @Header("type") String type) {
		String data = JsonFactory.buildJsonStr(payload, type, config)
		if (log.isDebugEnabled()) {
			log.debug("JSON message payload: ${data}")
		}
		final Message<String> message = MessageBuilder.withPayload(data)
		.setHeader("type", type)
		.build()
		return message
	}
}
