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

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.log4j.Logger
import org.springframework.integration.Message
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.annotation.Header
import org.springframework.integration.annotation.Payload
import org.springframework.integration.annotation.Transformer
import org.springframework.integration.support.MessageBuilder

import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants

import au.com.redboxresearchdata.util.script.ScriptExecutor;
/**
 * Transformer for transforming JSON string messages using Velocity templates.
 * 
 * Please set the 'config' property, with the ConfigObject instance containing the 'velocityTransformer.templateDir' and 'velocityTransformer.<type>.templates' array property.
 * 
 * This transformer also has pre and post Velocity execution scripting support.
 *  
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 * 
 */
class JsonVelocityTransformer {
	private static final Logger logger = Logger.getLogger(JsonVelocityTransformer.class)
	ConfigObject config	
	VelocityEngine ve
			
	private void initVelocity() {
		ve = new VelocityEngine()
		logger.debug("Template dir:${config.velocityTransformer.templateDir}")
		ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, config.velocityTransformer.templateDir)
		ve.init()
	}
	
	@Transformer
	public Message<String> handleMessage(final Message<String> inputMessage, @Header("type") String type) {
		String dataStr = inputMessage.getPayload()		
		String data = dataStr
		if (!ve) {
			initVelocity()
		}
		VelocityContext vc = new VelocityContext()
		vc.put("origData", dataStr)
		logger.debug("origData set to: ${dataStr}")		
		def dataMap = new JsonSlurper().parseText(dataStr)
		
		// run the pre execution scripts, passing in the dataMap, no validation is expected so checkData is false
		def scriptingReturnValue = ScriptExecutor.launchScripts(config.velocityTransformer[type]?.scripts?.preVelocity, false, dataMap, type, config)
		dataMap = scriptingReturnValue.data
		data = new JsonBuilder(dataMap).toString()
		vc.put("data", dataMap)
		
		config.velocityTransformer[type]?.templates?.each { templatePath->
			Template template = ve.getTemplate(templatePath)
			
			if (template) {				
				StringWriter sw = new StringWriter()
				vc.put("currentRendering", data)
				logger.debug("Executing: ${templatePath}")
				template.merge(vc, sw)
				data = sw.toString()
				sw.close()
			} else {
				logger.error("Failed to load template: ${templatePath}")
			}
		}		
		MessageBuilder builder = MessageBuilder.withPayload(data)
		inputMessage.getHeaders().keySet().each {k->
			if (!(MessageHeaders.ID.equals(k) || MessageHeaders.TIMESTAMP.equals(k))) {			
				builder.setHeader(k,inputMessage.getHeaders().get(k))
			}
		}
		// run the post execution scripts, instead of the JSONish structure, we pass in the message builder, so scripts can manipulate the message as they wish.
		scriptingReturnValue = ScriptExecutor.launchScripts(config.velocityTransformer[type]?.scripts?.postVelocity, false, builder, type, config)
		builder = scriptingReturnValue.data
		
		final Message<String> message = builder.build()				
		return message
	}
	
}
