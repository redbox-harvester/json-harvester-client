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
package au.com.redboxresearchdata.jdbc

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource

/**
 * Exposes harvest.jdbc[type].sqlParam[name] entries in the config as a parameter.
 * 
 * @author Shilo Banihit
 *
 */
class ConfigSqlParameterSource extends AbstractSqlParameterSource {
	private static final Logger log = Logger.getLogger(ConfigSqlParameterSource.class)
	
	ConfigObject config
	String type

	@Override
	public Object getValue(String name) throws IllegalArgumentException {
		def value = config.harvest.jdbc[type].sqlParam[name]
		log.debug("Returning ${name} = ${value}")
		return value 		
	}

	@Override
	public boolean hasValue(String name) {
		return config.harvest.jdbc[type].sqlParam[name] != null
	}

}
