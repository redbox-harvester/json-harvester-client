/**
 * CSVJDBC Configuration File
 * 
 * This sample configuration transforms and sends CSV files to a JMS Queue in JSON format.
 * 
 * It scans the "input" directory and processes files ending in 'csv' extension. The CSV file must have field names specified on the first line. 
 * It runs the scripts specified below before transforming these into JSON. The resulting JSON message is then sent to queue named "jsonHarvester" on the specified server and port below. 
 * The queue name is configured at the Spring Integration XML configuration. The processed CSV files are then moved to the "output" directory. 
 * 
 *      
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
// Cross-environment config below...
environment = environment

// Environment specific config below...
environments {
	development {
		client {
			harvesterId = "CsvJdbcHarvester"
			siPath = "sample/config/integration/spring-integration-csvjdbc.xml"
		}
		file {
			runtimePath = "src/test/resources/config/generated/config-csvjdbc.groovy"
			customPath = "src/test/resources/config/config-csvjdbc.groovy"
		}
		harvest {
			/* Please see http://csvjdbc.sourceforge.net/ for details on your options below. */
			csvjdbc {
				className = "org.relique.jdbc.csv.CsvDriver"
				url = "jdbc:relique:csv:class:au.com.redboxresearchdata.harvester.csvjdbc.CsvFileReader"
			}
			directory = "input"
			queueCapacity = 10
			pollRate = 5000
			output {
				directory = "output/"
				dateFormat = "yyyy-MM-dd_HHmmssSSS"
			}
			scripts {			
				//             "script path" : "configuration path" - pass in an emtpy string config path if you do not want to override the script's default config lookup behavior. 					
				preAssemble = [["scripts/missingfields.py":""], ["scripts/filter.groovy":""]]
			}
		}
		activemq {
			url = "tcp://localhost:9201"
		}
		
	}
	production {
		client {
			harvesterId = "CsvJdbcHarvester"
			siPath = "sample/config/integration/spring-integration-csvjdbc.xml"
		}
		file {
			runtimePath = "sample/config/generated/config-csvjdbc.groovy"
			customPath = "sample/config/config-csvjdbc.groovy"
		}
		harvest {
			/* Please see http://csvjdbc.sourceforge.net/ for details on your options below. */
			csvjdbc {
				className = "org.relique.jdbc.csv.CsvDriver"
				url = "jdbc:relique:csv:class:au.com.redboxresearchdata.harvester.csvjdbc.CsvFileReader"
			}
			directory = "input"
			queueCapacity = 10
			pollRate = 5000
			output {
				directory = "output/"
				dateFormat = "yyyy-MM-dd_HHmmssSSS"
			}
			scripts {
				//             "script path" : "configuration path" - pass in an emtpy string config path if you do not want to override the script's default config lookup behavior.
				preAssemble = [["scripts/missingfields.py":""], ["scripts/filter.groovy":""]]
			}
		}
		activemq {
			url = "tcp://localhost:9201"
		}
	}
}