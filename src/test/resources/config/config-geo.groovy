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
			runtimePath = "config/generated/config-geo.groovy"
			customPath = "config/config-geo.groovy"
		}
		harvest {
			jdbc {
				driver = "org.postgresql.Driver"
				url = "jdbc:postgresql://localhost:2001/aodn_portal"
				service { query = "SELECT dataset_name, description, collection_period_from, collection_period_to FROM metadata" }
				user="geodb"
				pw="geodb"
			}
			pollRate = "5000"
			queueCapacity = "1"
			pollTimeout = "5000"
		}
		defaultTemplate = """workflow {
				metadata {
					id = "test"
					step = "metadata-review"
					pageTitle = "Metadata Record"
					label = "Metadata Review"
				}
			}"""
		activemq { url = "tcp://localhost:9000" }
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
			runtimePath = "config/generated/config-geo.groovy"
			customPath = "config/config-geo.groovy"
		}
		harvest {
			jdbc {
				driver = "org.postgresql.Driver"
				url = "jdbc:postgresql://localhost:2001/aodn_portal"
				service { query = "SELECT dataset_name, description, collection_period_from, collection_period_to FROM metadata" }
				user="geodb"
				pw="geodb"
			}
			pollRate = "5000"
			queueCapacity = "1"
			pollTimeout = "5000"
		}
		defaultTemplate {
			workflow.metadata  {
				id = "test"
				step = "metadata-review"
				pageTitle = "Metadata Record"
				label = "Metadata Review"
			}
		}
		activemq { url = "tcp://118.138.242.150:9101" }
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