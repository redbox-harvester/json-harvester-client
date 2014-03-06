/**
 * CSVJDBC Configuration File
 * ----------------------------------------------------------------------
 * PROJECT: JSON File Harvester Client
 * ----------------------------------------------------------------------
 * 
 * @author Shilo Banihit
 *
 */
// Cross-environment config below...
environment = environment

// Environment specific config below...
environments {
	development {
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
		
	}
}