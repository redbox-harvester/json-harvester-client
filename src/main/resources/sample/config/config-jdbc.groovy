/**
 * JDBC Harvest Configuration File
 * ----------------------------------------------------------------------
 *
 * This sample configuration transforms and sends data from a MySQL database to a JMS Queue in JSON format.
 * 
 * It executes the query specified on the "harvest.jdbc.[Data Type Name]" entry below. The data type configured for this example is "Dataset", and is found on the Spring Integration XML file.
 * For each of the record returned, the data is transformed into JSON. Prior to creating the JSON, the scripts specified at "harvest.scripts.preAssemble" are executed. 
 * After the JSON message is constructed, the scripts specified at "harvest.scripts.postBuild" are executed.
 * 
 * After all the records had been transformed into JSON, the data is sent to the JMS queue "jsonHarvester" on the server and port configured below. The queue name is specified at the Spring Integration XML file.
 * 
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
environments {
	development {
		client {
			harvesterId = "JdbcHarvester"
			siPath = "sample/config/integration/spring-integration-jdbc.xml"
		}
		file {
			runtimePath = "sample/config/generated/config-jdbc.groovy"
			customPath = "sample/config/config-jdbc.groovy"
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
		client {
			harvesterId = "JdbcHarvester"
			siPath = "sample/config/integration/spring-integration-jdbc.xml"
		}
		file {
			runtimePath = "sample/config/generated/config-jdbc.groovy"
			customPath = "sample/config/config-jdbc.groovy"
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