/**
 * JDBC Harvest Configuration File
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
			runtimePath = "src/test/resources/config/generated/config-jdbc.groovy"
			customPath = "src/test/resources/config/config-jdbc.groovy"
		}
		jdbc {
			driver = "com.mysql.jdbc.Driver"
			url = "jdbc:mysql://localhost/jdbc_harvester"
			user = "user"
			pw = "user"
			service {
				query = "SELECT * FROM services_view"
				update = "UPDATE services set Processed='Y' where id in (:id)"
			}
			poller {
				max-messages-per-poll = "1"
				receive-timeout = "5000"
				fixed-delay = "5000"
			}
		}
		activemq {
			url = "tcp://localhost:9201"
		}
	}
	production {
		file {
			runtimePath = "config/generated/config-csvjdbc.groovy"
			customPath = "config/config-csvjdbc.groovy"
		}
		jdbc {
			driver = "com.mysql.jdbc.Driver"
			url = "jdbc:mysql://localhost/jdbc_harvester"
			user = "user"
			pw = "user"
			inbound {
				query = "SELECT * FROM services_view"
				update = "UPDATE services set Processed='Y' where id in (:id)"
			}
			poller {
				max-messages-per-poll = "1"
				receive-timeout = "5000"
				fixed-delay = "5000"
			}
		}
		activemq {
			url = "tcp://localhost:9201"
		}
	}
}