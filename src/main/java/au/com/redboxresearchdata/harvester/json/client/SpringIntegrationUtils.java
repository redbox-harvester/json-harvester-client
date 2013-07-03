/*******************************************************************************
 * Copyright (C) 2013 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package au.com.redboxresearchdata.harvester.json.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;

/**
 * Displays the names of the input and output directories.
 *
 * @author Shilo Banihit
 * @since 1.0
 *
 */
public final class SpringIntegrationUtils {

	private static final Log logger = LogFactory.getLog(SpringIntegrationUtils.class);

	private SpringIntegrationUtils() { }

	/**
	 * Helper Method to dynamically determine and display input and output
	 * directories as defined in the Spring Integration context.
	 *
	 * @param context Spring Application Context
	 */
	public static void displayDirectories(final ApplicationContext context) {

		final File inDir = (File) new DirectFieldAccessor(context.getBean(FileReadingMessageSource.class)).getPropertyValue("directory");

		final Map<String, FileWritingMessageHandler> fileWritingMessageHandlers = context.getBeansOfType(FileWritingMessageHandler.class);

		final List<String> outputDirectories = new ArrayList<String>();

		for (final FileWritingMessageHandler messageHandler : fileWritingMessageHandlers.values()) {
			final Expression outDir = (Expression) new DirectFieldAccessor(messageHandler).getPropertyValue("destinationDirectoryExpression");
			outputDirectories.add(outDir.getExpressionString());
		}

		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("\n=========================================================");
		stringBuilder.append("\n");
		stringBuilder.append("\n    Input directory is : '" + inDir.getAbsolutePath() + "'");

		for (final String outputDirectory : outputDirectories) {
			stringBuilder.append("\n    Output directory is: '" + outputDirectory + "'");
		}

		stringBuilder.append("\n\n=========================================================");

		logger.info(stringBuilder.toString());

	}

}
