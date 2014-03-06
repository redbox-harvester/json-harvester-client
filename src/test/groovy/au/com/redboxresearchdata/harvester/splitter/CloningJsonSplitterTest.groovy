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

import au.com.redboxresearchdata.harvester.json.splitter.CloningJsonSplitter
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.log4j.Logger;
import org.springframework.integration.Message
import org.springframework.integration.support.MessageBuilder

/**
 * Tests the Cloning vats..
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class CloningJsonSplitterTest extends GroovyTestCase{
	private static final Logger logger = Logger.getLogger(CloningJsonSplitterTest.class)
	
	void testCloner() {
		def config = ["cloningSplitter":["srcArray":"data.metadataPrefix", "modifyHeader":"type", "entryHeader":"metadataPrefix"]]
		def cloner = new CloningJsonSplitter()
		cloner.config = config
		def mapReq = [
			"data":[
				"field1":"value1",
				"metadataPrefix":["prefix1", "prefix2"]
			]
		]
		Message<String> inputMessage = MessageBuilder.withPayload(new JsonBuilder(mapReq).toString()).setHeader("type", "record").setHeader("specialHeader1", "value1").build()
		logger.debug(inputMessage)
		List<Message<String>> list = cloner.cloneByArray(inputMessage)
		logger.debug(list)
		assertNotNull(list)
		assertEquals(2, list.size())
		list.each {entry->
			def jsonEntry = new JsonSlurper().parseText(entry.getPayload())
			assertEquals(mapReq.data.field1, jsonEntry.data.field1)
			assertTrue(entry.getHeaders().get("type") == "record_prefix1" || entry.getHeaders().get("type") == "record_prefix2")
		}
	}
}
