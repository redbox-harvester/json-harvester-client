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
package au.com.redboxresearchdata.harvester.splitter

import groovy.json.JsonSlurper
import org.apache.log4j.Logger;
import org.springframework.integration.Message
import org.springframework.integration.MessageHeaders
import org.springframework.integration.support.MessageBuilder

import au.com.redboxresearchdata.harvester.json.splitter.GenericJsonSplitter;

/**
 * 
 * Testing the Splitter...
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class GenericJsonSplitterTest extends GroovyTestCase {
	private static final Logger logger = Logger.getLogger(GenericJsonSplitter.class)
		
	void testSplitter() {
		GenericJsonSplitter splitter = new GenericJsonSplitter()
		def copyFields = new ArrayList<String>()
		copyFields << "field1"
		splitter.copyPayloadFieldsToHeader = copyFields
		def request = """
		{
	  		"header": {
	 			"header1":"value1",
	 			"headerN":"valueN" 			 			
	  		},
	  		"data": [
	  			{
	  				"field1":"value1",
	  				"fieldN":"valueN"
	  			},
				{
	  				"fieldX":"valueX",
	  				"fieldN":"valueN"
	  			}
	  		]	  
		}
		"""
		Message<String> inputMessage = MessageBuilder.withPayload(request).setHeader("header1", "header1invalid").setHeader("specialHeader1", "value1").build()
		List<Message<String>> list = splitter.parseRequest(inputMessage)
		assertNotNull(list)
		assertEquals(2, list.size())
		def foundX = false
		def foundY = false
		list.each {message->
			MessageHeaders headers = message.getHeaders()
			assertTrue(headers.containsKey("header1"))
			assertEquals("value1", headers.get("header1"))
			assertTrue(headers.containsKey("headerN"))
			assertEquals("valueN", headers.get("headerN"))
			assertTrue(headers.containsKey("specialHeader1"))
			assertEquals("value1", headers.get("specialHeader1"))
			
			String payload = message.getPayload()
			assertNotNull(payload)
			logger.debug("Payload is: ${payload}")		
			def payloadJson = new JsonSlurper().parseText(payload)
			
			if (payloadJson.fieldX != null) {
				assertEquals("valueX", payloadJson.fieldX)
				foundX = true
				assertNull(headers.get("field1"))
			} else {
				assertEquals("value1", payloadJson.field1)
				assertEquals("value1", headers.get("field1"))
			}
			assertEquals("valueN", payloadJson.fieldN)
		}
		assertTrue(foundX)
		assertFalse(foundY)
	}
}
