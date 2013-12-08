/**
 * JDBC Harvest Configuration File
 * ----------------------------------------------------------------------
 * PROJECT: JSON File Harvester Client
 * ----------------------------------------------------------------------
 *
 * @author Shilo Banihit
 *
 */
Service {
	fields = [
		["ID" : "ID"],
		["Name" : "name"],
		["Type" : "type"],
		["ANZSRC_FOR_1" : "ANZSRC_FOR_1"],
		["ANZSRC_FOR_2" : "ANZSRC_FOR_2"],
		["ANZSRC_FOR_3" : "ANZSRC_FOR_3"],
		["Location" : "Location"],
		["Coverage_Temporal_From" : "Coverage_Temporal_From"],
		["Coverage_Temporal_To" : "Coverage_Temporal_To"],
		["Coverage_Spatial_Type" : "Coverage_Spatial_Type"],
		["Coverage_Spatial_Value" : "Coverage_Spatial_Value"],
		["Existence_Start" : "Existence_Start"],
		["Existence_End" : "Existence_End"],
		["Website" : "Website"],
		["Data_Quality_Information" : "Data_Quality_Information"],
		["Reuse_Information" : "Reuse_Information"],
		["Access_Policy" : "Access_Policy"],
		["Description" : "Description"],
		["URI" : "URI"]
	]
	required = ["ID"]
}
Dataset {
	fields = [
		["id" : "datasetId"]
	]
}
// Environment specific config below...
environments {
	development {
		file {
			runtimePath = "config/generated/config-jdbc.groovy"
			customPath = "config/config-jdbc.groovy"
		}
		harvest {
			jdbc {
				driver = "com.mysql.jdbc.Driver"
				url = "jdbc:mysql://localhost/jdbc_harvester"				
				Dataset {
					query = "SELECT * FROM dataset WHERE last_updated >= TIMESTAMP(:last_harvest_ts)"
					sqlParam {
						last_harvest_ts = "2013-10-10 00:00:00"
					}					
				}							
			}
			pollRate = "5000"
			queueCapacity = "10"
			pollTimeout = "5000"
			scripts {
				//             "script path" : "configuration path" - pass in an emtpy string config path if you do not want to override the script's default config lookup behavior.
				preBuild = [] // executed after a successful, but prior to building the JSON String, no data is passed
				preAssemble = [["scripts/merge.groovy":""]] // executed prior to building the JSON string, each resultset (map) of the JDBC poll is passed as 'data'
				postBuild = [["scripts/update_last_harvest_ts.groovy":""],["scripts/saveconfig.groovy":""]] // executed after the data is processed, but prior to ending the poll
			}
		}
		activemq {
			url = "tcp://localhost:9101"
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
				dataset {
					query = "SELECT * FROM services_view"
					update = "UPDATE services Processed='Y' where id in (:id)"
				}							
			}
			pollRate = "5000"
			queueCapacity = "10"
			pollTimeout = "5000"
		}
		activemq {
			url = "tcp://localhost:9101"
		}
	}
}