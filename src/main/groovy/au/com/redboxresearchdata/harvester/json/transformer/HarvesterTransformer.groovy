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
import au.com.redboxresearchdata.util.script.ScriptExecutor
import au.com.redboxresearchdata.json.JsonFactory

/**
 * 
 * Transformer that executes a configured set of scripts to transform the message object from the source 
 * inbound channel format to another.
 *
 * For example, transform a JSON string to a map so that it can be manipulated later.
 *  
 * @author <a href="https://github.com/andrewbrazzatti" target="_blank">Andrew Brazzatti</a>
 *
 */
class HarvestTransformer {

	private static final Logger log = Logger.getLogger(JsonTransformationHandler.class)
	ConfigObject config
	
	/**
	 * 
	 * 
	 * @param inputMessage
	 * @return SI Message with String payload.
	 */
	@Transformer
	Message transform(final Message inputMessage) {
	   def scriptBase = config.harvest.scripts?.scriptBase ? config.harvest.scripts.scriptBase  : ""
	   ScriptExecutor.launchScripts(scriptBase, config.harvest.scripts?.preBuild, false, null, null, config) 
	   def preAssembleResults = ScriptExecutor.launchScripts(scriptBase, config.harvest.scripts?.preAssemble, true, inputMessage, null, config)
	   if(preAssembleResults.data != null ) {
	   		final Message message = preAssembleResults.data;
	   		println "This is the final message"
	   		println message.getPayload().getClass()
	   		ScriptExecutor.launchScripts(scriptBase, config.harvest.scripts?.postBuild, false, null, null, config)
	   		return message
	   } else {
	   	
	   	ScriptExecutor.launchScripts(scriptBase, config.harvest.scripts?.postBuild, false, null, null, config)
	   	return null;
	   }

			
		
	}
}
