/*
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
 */
package au.com.redboxresearchdata.harvester.json.splitter

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import java.util.List
import org.springframework.integration.Message
import org.springframework.integration.annotation.Splitter
import org.springframework.integration.support.MessageBuilder
import org.springframework.integration.MessageHeaders;
import groovy.util.Eval
/**
 * Clones the incoming JSON by examining the field specified at 'config.cloningSplitter.srcArray' entry.
 * 
 *  The elements of the field are then appended to the header specified at 'config.cloningSplitter.modifyHeader', prefixed with "_".
 * 
 * @author <a href="https://github.com/shilob" target="_blank">Shilo Banihit</a>
 *
 */
class CloningJsonSplitter {
	ConfigObject config
	
	@Splitter
	public List<Message<String>> cloneByArray(final Message<String> inputMessage) {		
		ArrayList<Message<String>> reqList = new ArrayList<Message<String>>()
		
		def json = new JsonSlurper().parseText(inputMessage.getPayload())
		def srcArray = Eval.x(json, "x.${config.cloningSplitter?.srcArray}") 
		if (srcArray) {
			srcArray.each { entry->
				def clone = json.clone()
				def modHeader = "${inputMessage.getHeaders().get(config.cloningSplitter.modifyHeader)}_${entry}" 
				Eval.x(clone, "x.${config.cloningSplitter?.srcArray} = '${modHeader}'")
				
				MessageBuilder<String> builder = MessageBuilder.withPayload(new JsonBuilder(clone).toString())
				copyHeaders(inputMessage, builder)
				builder.setHeader(config.cloningSplitter.modifyHeader, modHeader)
				builder.setHeader(config.cloningSplitter.entryHeader, entry)
				reqList << builder.build()		
			}
		} else {
			MessageBuilder<String> builder = MessageBuilder.withPayload(new JsonBuilder(json).toString())
			copyHeaders(inputMessage, builder)
			reqList << builder.build()
		}
		return reqList
	}
	
	def copyHeaders(inputMessage, builder) {
		// copying the existing values in the header...
		inputMessage.getHeaders().keySet().each {k->
			if (!(MessageHeaders.ID.equals(k) || MessageHeaders.TIMESTAMP.equals(k))) {
				builder.setHeader(k,inputMessage.getHeaders().get(k))
			}
		}
	}
}
