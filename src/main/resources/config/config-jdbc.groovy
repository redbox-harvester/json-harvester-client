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
				driver = "org.postgresql.Driver"
				url = "jdbc:postgresql://localhost:2001/aodn_portal"			
				service {
					query = "SELECT dataset_name, description, collection_period_from, collection_period_to FROM metadata"
				}
				user="geodb"
				pw="geodb"		
			}
			pollRate = "5000"
			queueCapacity = "1"
			pollTimeout = "5000"
		}
		activemq {
			url = "tcp://118.138.242.150:9101"
		}
		types  {
			Service  {
				fields = [
					["dataset_name":"Name"],
					["description":"Description"],
					["collection_period_from":"Coverage_Temporal_From"],
					["collection_period_to":"Coverage_Temporal_To"]
				]	
			}
		}
	}
	production {
		file {
			runtimePath = "config/generated/config-jdbc.groovy"
			customPath = "config/config-jdbc.groovy"
		}
		harvest {
			jdbc {
				driver = "org.postgresql.Driver"
				url = "jdbc:postgresql://localhost:2001/aodn_portal"			
				service {
					query = "SELECT dataset_name, description, collection_period_from, collection_period_to FROM metadata"
				}		
				user="geodb"
				pw="geodb"
			}
			pollRate = "5000"
			queueCapacity = "1"
			pollTimeout = "5000"
		}
		activemq {
			url = "tcp://118.138.242.150:9101"
		}
		types  {
			Service  {
				fields = [
					["dataset_name":"Name"],
					["description":"Description"],
					["collection_period_from":"Coverage_Temporal_From"],
					["collection_period_to":"Coverage_Temporal_To"]
				]	
			}
		}
	}
}