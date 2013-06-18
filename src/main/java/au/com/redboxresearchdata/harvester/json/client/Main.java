package au.com.redboxresearchdata.harvester.json.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Starts the Spring Context and will initialize the Spring Integration routes.
 *
 * @author Shilo Banihit
 * @since 1.0
 *
 */
public final class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);
	private static final String[] clientTypeArr = {"file", "jdbc","csvjdbc"};
	private static final ArrayList<String> clientTypes = new ArrayList<String>(Arrays.asList(clientTypeArr));

	private Main() { }

	private static void displayOptions() {
		LOGGER.info("Please select an option: " + clientTypes.toString());
	}
	/**
	 * Load the Spring Integration Application Context
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
		if (!clientTypes.contains(clientType)) {
			LOGGER.error("Invalid client type!");
			displayOptions();
			return;
		}
		String contextFile = "spring-integration-"+clientType+".xml";
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("\n========================================================="
					  + "\n                                                         "
					  + "\n     Welcome to ReDBox / Mint JSON Harvester Client      "
					  + "\n                                                         "
					  + "\n     You have selected: "+ clientType 
					  + "\n     Using configuration file: " + contextFile
					  + "\n                                                         "
					  + "\n=========================================================" );
		}

		final AbstractApplicationContext context =
				new ClassPathXmlApplicationContext("classpath:META-INF/spring/integration/" + contextFile);

		context.registerShutdownHook();

		if (clientType.equals("file")) {
			SpringIntegrationUtils.displayDirectories(context);
		}
		
		if (clientType.equals("file") || clientType.equals("jdbc")) {
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
		} else if (clientType.equals("csvjdbc")) {			
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
