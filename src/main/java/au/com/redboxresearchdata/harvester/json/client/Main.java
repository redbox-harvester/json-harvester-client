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

import groovy.util.ConfigObject;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import au.com.redboxresearchdata.util.config.Config;


/**
 * Starts the Spring Context and will initialize the Spring Integration routes. 
 * 
 * The class expects a single command line argument: the configuration file path. 
 * 
 * It also expects the ff. system properties: 
 * 
 * "environment" - string qualifier used in the environment-aware configuration.
 * "run.mode" - See description below.
 * 
 * This class attempts to load the default config file specified at the command path. It initially expects this file to be present on the file system. Failing this, it will attempt to load the same default config file path from the classpath .
 * 
 * The configuration value at "client.siPath" specifies the Spring Integration Application Context File. This file may exist in the file system, if not, it will attempt to load the file from the classpath. 
 * 
 * The run mode dictates how the client behaves:
 * 
 * "daemon" - interactive mode, will continue to poll until "q" is pressed. 
 * 
 * Otherwise, the client will continue to run while the Spring application context is running or while the main thread isn't interrupted while sleeping.
 *
 * @author Shilo Banihit
 * @since 1.0
 *
 */
public final class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);
	

	private Main() { }

	
	/**
	 * Load the Spring Integration Application Context. 
	 *
	 * @param args - command line arguments
	 */
	public static void main(final String... args) {
		
		if (args.length <= 0) {
			LOGGER.error("Configuration path not provided!");
			return;
		}
		String configFilePath = args[0];
				
		String environment = System.getProperty("environment");
		String runMode = System.getProperty("run.mode");
		
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("\n========================================================="
					  + "\n                                                         "
					  + "\n     Welcome to ReDBox / Mint JSON Harvester Client      "
					  + "\n                                                         "
					  + "\n     Using default configuration file: " + configFilePath
					  + "\n     Using environment: " + environment
					  + "\n                                                         "
					  + "\n=========================================================" );
		}

		ConfigObject config = Config.getConfig(environment, configFilePath);
		Map configMap = config.flatten();
		System.setProperty("environment", environment);
		System.setProperty("harvester.client.config.file", (String) configMap.get("file.runtimePath"));
		
		String contextFilePath = (String) configMap.get("client.siPath");
		// check if the harvester has a name
		String harvesterId = (String) configMap.get("client.harvesterId");
		if (harvesterId == null || harvesterId.length() == 0) {
			LOGGER.error("Invalid harvester configuration, no value specified at 'client.harvesterId'");
			return;
		}
		
		String absContextPath = contextFilePath;
		File contextFile = new File(absContextPath);
		final AbstractApplicationContext context;
		if (!contextFile.exists()) {
			absContextPath = "classpath:"+absContextPath;
			LOGGER.info("Using contextPath:" + absContextPath);
			context =
					new ClassPathXmlApplicationContext(absContextPath);
		} else {
			absContextPath = "file:" + absContextPath;
			LOGGER.info("Using contextPath:" + absContextPath);
			context =
					new FileSystemXmlApplicationContext(absContextPath);
		}
		
		
		
		context.registerShutdownHook();
		
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
