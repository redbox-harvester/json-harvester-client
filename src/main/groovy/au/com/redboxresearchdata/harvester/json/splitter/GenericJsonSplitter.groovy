/*******************************************************************************
 *Copyright (C) 2014 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
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
package au.com.redboxresearchdata.harvester.json.splitter

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.springframework.integration.Message
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.annotation.Payload
import org.springframework.integration.annotation.Splitter
import org.springframework.integration.support.MessageBuilder
/**
 * 
 * Convenience class that parses the incoming JSON String into one or more messages.  
 * 
 * See sample JSON structure below:
 *  
 * {
 * 		"header": {
 *			"header1":"value1",
 *			"headerN":"valueN" 			 			
 * 		},
 * 		"data": [
 * 			{
 * 				"field1":"value1",
 * 				"fieldN":"valueN"
 * 			}
 * 		]	
 * 
 * }
 * 
 * The "header" section will be parsed and populated into the message's headers, with the individual elements of the data array as String payload.
 * 
 *  The "copyPayloadFieldsToHeader" is a list of payload field names that should be copied to the header when these exist in the current element.
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class GenericJsonSplitter {
	List<String> copyPayloadFieldsToHeader
	
	@Splitter
	public List<Message<String>> parseRequest(final Message<String> inputMessage) {
		String jsonStr = inputMessage.getPayload()
		ArrayList<Message<String>> reqList = new ArrayList<Message<String>>()
		def json = new JsonSlurper().parseText(jsonStr)			
		// parsing data...
		json.data.each {d->
			MessageBuilder<String> builder = MessageBuilder.withPayload(new JsonBuilder(d).toString())
			// copying the existing values in the header...
			inputMessage.getHeaders().keySet().each {k->
				if (!(MessageHeaders.ID.equals(k) || MessageHeaders.TIMESTAMP.equals(k))) {
					builder.setHeader(k,inputMessage.getHeaders().get(k))
				}
			}
			// parsing header in data, overrides whatever was set earlier
			json.header.keySet().each { headerKey->				
				builder.setHeader(headerKey, json.header[headerKey])				
			}
			if (copyPayloadFieldsToHeader) {
				copyPayloadFieldsToHeader.each { payloadKey->
					if (d[payloadKey]) {
						builder.setHeader(payloadKey, d[payloadKey])
					}
				}
			}
			// finally store the parsed json in the header
			builder.setHeader("json", d)
			reqList << builder.build()
		}		
		return reqList
	}
}
