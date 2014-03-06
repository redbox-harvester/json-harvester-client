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
package au.com.redboxresearchdata.harvester.json.transformer

import groovy.json.JsonSlurper
import org.springframework.integration.Message
import org.springframework.integration.support.MessageBuilder
import org.apache.log4j.Logger

/**
 * Tests for the Transformer.
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class JsonVelocityTransformerTest extends GroovyTestCase {

	private static final Logger logger = Logger.getLogger(JsonVelocityTransformerTest.class)
	
	void testTransform() {
		JsonVelocityTransformer transformer = new JsonVelocityTransformer()
		def config = new ConfigSlurper("test").parse(new File("src/test/resources/config/config-unit-testing-velocityTransformer.groovy").toURI().toURL())
		transformer.config = config		
		def data = "{\"data\":\"test-data\", \"entry1\":\"entryval\"}" 
		logger.debug("In test method, data is: ${data}")
		Message<String> inputMessage = MessageBuilder.withPayload(data).setHeader("header1", "value1").setHeader("header2", "value2").build()
		Message<String> message = transformer.handleMessage(inputMessage, "testData")
		assertNotNull(message)		
		logger.debug(message)
		String jsonStr = message.getPayload()
		assertNotNull(jsonStr)
		def json = new JsonSlurper().parseText(jsonStr)
		assertNotNull(json)
		assertEquals(json.container2.container1.data, "test-data")
		assertEquals(json.container2.container1.aNewEntry, "aNewEntry")
		assertEquals(json.container2.container1.entry1, "entry1")
		assertEquals("value1", message.getHeaders().get("header1"))		
		assertEquals("value2", message.getHeaders().get("header2"))
		assertEquals("value3", message.getHeaders().get("header3"))
	}
}
