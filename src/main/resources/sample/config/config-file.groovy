/**
 * JSON file harvest Configuration File
 * ----------------------------------------------------------------------
 * 
 * This sample configuration scans the "input" directory for any JSON (.json extension) files and sends the contents of these files without transformations/modifications, 
 * to the configured a queue named "jsonHarvester" on the server and port specified below. The queue name is configured at the Spring Integration XML configuration. 
 * 
 * Since this configuration is for demonstration purposes, the JSON files stay on the 'input' directory and will get reprocessed on restart. All other files are moved to the "output/other" directory.  
 *
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
// Environment specific config below...
environments {
	development {
		client {
			harvesterId = "JsonFileHarvester"
			siPath = "sample/config/integration/spring-integration-file.xml"
		}
		file {
			runtimePath = "sample/config/runtime/config-file.groovy"
			customPath = "sample/config/custom/config-file.groovy"
		}
		harvest {
			directory = "input"
			pollRate = "5000"
			queueCapacity = "10"
			output {
				json {
					directory = "target/output/json"
					deletesource = "false"
				}
				other {
					directory = "target/output/other"
					deletesource = "true"
				}
			}
		}
		activemq {
			url = "tcp://localhost:9101"
		}
	}
	production {
		client {
			harvesterId = "JsonFileHarvester"
			siPath = "sample/config/integration/spring-integration-file.xml"
		}
		file {
			runtimePath = "sample/config/generated/config-file.groovy"
			customPath = "sample/config/config-custom-file.groovy"
		}
		harvest {
			directory = "input"
			pollRate = "5000"
			queueCapacity = "10"
			output {
				json {
					directory = "output/json"
					deletesource = "true"
				}
				other {
					directory = "output/other"
					deletesource = "true"
				}
			}
		}
		activemq {
			url = "tcp://localhost:9101"
		}
	}
}