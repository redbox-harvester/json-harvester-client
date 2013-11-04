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
package au.com.redboxresearchdata.harvester.service

import groovy.json.JsonBuilder;

import org.apache.log4j.Logger
import org.springframework.integration.Message
import org.springframework.integration.annotation.ServiceActivator

/* 
 * @author Matt Mulholland
 */
class JsonTemplateService {

	private static final Logger log = Logger.getLogger(JsonTemplateService.class);
	ConfigObject config;
	
	/**
	 * Default file handler: loads the contents of the file as message payload.
	 * 
	 * Sets the filename, file size, file extension, filename without extension as headers.
	 * It also sets the FileHeaders.ORIGINAL_FILE File reference.
	 * 
	 * @param inputMessage
	 * @return
	 */
	@ServiceActivator
	public Message<String> handleFile(final Message<String> inputMessage) {
		String inputString = inputMessage.getPayload();
		JsonBuilder builder = new JsonBuilder();
		String jsonTemplate = "{\n" + 
				" \"type\":\"DatasetJson\", \n" + 
				" \"data\": {\n" + 
				"    \"data\": [\n" + 
				"        {\n" + 
				"        \"datasetId\":\"1\"," +
				"   }      ]   }}";

		builder.
				
		return message;
	}
}