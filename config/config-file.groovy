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
		file {
			runtimePath = "src/test/resources/config/generated/config-file.groovy"
			customPath = "src/test/resources/config/config-file.groovy"
		}
		harvest {
			directory = "input"
			pollRate = "5000"
			queueCapacity = "10"
			output {
				json {
					directory = "target/output/json"
					deletesource = "true"
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
		file {
			runtimePath = "config/generated/config-file.groovy"
			customPath = "config/config-custom-file.groovy"
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