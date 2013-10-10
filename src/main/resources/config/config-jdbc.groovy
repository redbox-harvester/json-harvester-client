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
		harvest {
			jdbc {
				driver = "com.mysql.jdbc.Driver"
				url = "jdbc:mysql://localhost/jdbc_harvester"				
				service {
					query = "SELECT * FROM services_view"
					update = "UPDATE services set Processed='Y' where id in (:id)"
				}							
			}
			pollRate = "5000"
			queueCapacity = "1"
			pollTimeout = "5000"
		}
		activemq {
			url = "tcp://localhost:9201"
		}
	}
	production {
		file {
			runtimePath = "config/generated/config-jdbc.groovy"
			customPath = "config/config-jdbc.groovy"
		}
		harvest {
			jdbc {
				driver = "com.mysql.jdbc.Driver"
				url = "jdbc:mysql://localhost/jdbc_harvester"				
				service {
					query = "SELECT * FROM services_view"
					update = "UPDATE services set Processed='Y' where id in (:id)"
				}							
			}
			pollRate = "5000"
			queueCapacity = "10"
			pollTimeout = "5000"
		}
		activemq {
			url = "tcp://localhost:9201"
		}
	}
}