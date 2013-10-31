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
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import au.com.redboxresearchdata.util.config.Config;


/**
 * Starts the Spring Context and will initialize the Spring Integration routes. 
 * 
 * The class expects a single command line argument: the client type. It also expects an system property: "environment", that is used in the environment-aware configuration.
 * 
 * This class attempts to load the default config file from the current working directory: "config/config-<client type command line argument>.groovy".
 * Failing this, it will finally load the same default config file path from the classpath .
 * 
 * The client type argument controls which integration routes are loaded, located at "config/integration/spring-integration-<client type>.xml". 
 * The relative path is initially loaded from the current working directory. Failing this, it will load it from the classpath.
 * 
 * The client type argument also dictates how this class behaves:
 * 
 * "file", "jdbc" - interactive mode, will continue to poll until "q" is pressed.
 * "csvjdbc" - will process all valid files in the "{harvest.directory}" and then exit.
 *
 * @author Shilo Banihit
 * @since 1.0
 *
 */
public final class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);
	

	private Main() { }

	private static void displayOptions() {
		//TODO: determine list of options
//		LOGGER.info("Please select an option: " + clientTypes.toString());
	}
	/**
	 * Load the Spring Integration Application Context. 
	 *
	 * @param args - command line arguments
	 */
	public static void main(final String... args) {
		
		if (args.length <= 0) {
			LOGGER.error("No client type!");
			displayOptions();
			return;
		}
		String clientType = args[0];
		
		String contextFilePath = "spring-integration-"+clientType+".xml";
		String configFilePath = "config/config-" +clientType+".groovy";
		String environment = System.getProperty("environment");
		String runMode = System.getProperty("run.mode");
		
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("\n========================================================="
					  + "\n                                                         "
					  + "\n     Welcome to ReDBox / Mint JSON Harvester Client      "
					  + "\n                                                         "
					  + "\n     You have selected: "+ clientType 
					  + "\n     Using context configuration file: " + contextFilePath
					  + "\n     Using default configuration file: " + configFilePath
					  + "\n     Using environment: " + environment
					  + "\n                                                         "
					  + "\n=========================================================" );
		}

		System.setProperty("environment", environment);
		System.setProperty("harvester.client.config.file", configFilePath);
		
		Config.getConfig(environment, configFilePath);
		
		String absContextPath = "config/integration/" + contextFilePath;
		File contextFile = new File(absContextPath);
		final AbstractApplicationContext context;
		if (!contextFile.exists()) {
			absContextPath = "classpath:"+absContextPath;
			context =
					new ClassPathXmlApplicationContext(absContextPath);
		} else {
			absContextPath = "file:" + absContextPath; 
			context =
					new FileSystemXmlApplicationContext(absContextPath);
		}
		

		context.registerShutdownHook();

		if (clientType.equals("file")|| clientType.equals("riffile")) {
			SpringIntegrationUtils.displayDirectories(context);
		}
		
		if ("daemon".equals(runMode)) {
			final Scanner scanner = new Scanner(System.in);
	
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("\n========================================================="
						  + "\n                                                         "
						  + "\n    Please press 'q + Enter' to quit the application.    "
						  + "\n                                                         "
						  + "\n=========================================================" );
			}
			while (!scanner.hasNext("q")) {
				//Do nothing unless user presses 'q' to quit.
			}
			scanner.close();
		} else {			
			try {
				while (context.isRunning()) {
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				LOGGER.info("Interrupted, shutting down...");
			}
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Exiting application...bye.");
		}
		
		System.exit(0);
	}
}
