/**
 * JSON file harvest Configuration File
 * ----------------------------------------------------------------------
 * PROJECT: JSON File Harvester Client
 * ----------------------------------------------------------------------
 *
 * @author Shilo Banihit
 *
 */
// Environment specific config below...
environments {
	development {
		client {
			harvesterId = "JsonFileHarvester"
			siPath = "config/integration/spring-integration-file.xml"
		}
		file {
			runtimePath = "config/runtime/config-file.groovy"
			customPath = "config/custom/config-file.groovy"
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
			siPath = "config/integration/spring-integration-file.xml"
		}
		file {
			runtimePath = "config/runtime/config-file.groovy"
			customPath = "config/custom/config-file.groovy"
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
}