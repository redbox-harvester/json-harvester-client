environments {
	development {
		file {
			runtimePath = "src/test/resources/config/generated/config-csvjdbc.groovy"
			customPath = "src/test/resources/config/config-csvjdbc.groovy"
		}
		harvest {
			directory = "input"
			queueCapacity = 10
			pollRate = 5000
			output {
				directory = "output/"
				dateFormat = "yyyy-MM-dd_HHmmssSSS"
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
		harvest {
			directory = "input"
			queueCapacity = 100
			output {
				directory = "output/"
				dateFormat = "yyyy-MM-dd_HHmmssSSS"
			}
		}
		activemq {
			url = "tcp://localhost:9201"
		}
	}
}