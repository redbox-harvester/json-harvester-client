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
package au.com.redboxresearchdata.harvester.json.service

import groovy.json.JsonBuilder

import org.apache.log4j.Logger
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.integration.Message
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.support.MessageBuilder;

import com.google.gson.JsonParser;

import au.com.redboxresearchdata.harvester.utilities.HarvestUtilities;

/* 
 * @author Matt Mulholland
 */
class JsonTemplateService {

	private static final Logger log = Logger.getLogger(JsonTemplateService.class);
	ConfigObject config;


	@ServiceActivator
	public Message<String> addDefaultValues(final Message<String> inputMessage) {
		String inputString = inputMessage.getPayload();

		def defaultTemplate = config.defaultTemplate
		JSONObject jsonDefaults = new JSONObject(defaultTemplate)
		String type = config.defaultType

		String result = HarvestUtilities.addDefaultToWrappedRecords(inputString, jsonDefaults.toString(), type)

		final Message<String> message = MessageBuilder.withPayload(result).build()
		return message;
	}
}