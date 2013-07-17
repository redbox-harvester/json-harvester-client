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
			poller {
				max-messages-per-poll = "10"
				receive-timeout = "5000"
			}
			output {
				json {
					directory = "target/output/json"
					delete-source-files = "true"
				}
				other {
					directory = "target/output/other"
					delete-source-files = "true"
				}
			}
		}
		activemq {
			url = "tcp://localhost:9201"
		}
	}
	production {
		file {
			runtimePath = "config/generated/config-file.groovy"
			customPath = "config/config-file.groovy"
		}
		harvest {
			directory = "input"
			poller {
				max-messages-per-poll = "10"
				receive-timeout = "5000"
			}
			output {
				json {
					directory = "target/output/json"
					delete-source-files = "true"
				}
				other {
					directory = "target/output/other"
					delete-source-files = "true"
				}
			}
		}
		activemq {
			url = "tcp://localhost:9201"
		}
	}
}