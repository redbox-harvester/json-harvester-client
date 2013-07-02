environments {
	development {
		file {
			runtimePath = "src/test/resources/config/generated/config-csvjdbc.groovy"
			customPath = "src/test/resources/config/config-csvjdbc.groovy"
		}
		harvest {
			directory = "input"
			queueCapacity = 10
			output {
				directory = "output/"
				dateFormat = "yyyy-MM-dd_HHmmssSSS"
			}
		}
	}
	production {
		file {
			runtimePath = "config/generated/config-csvjdbc.groovy"
			customPath = "config/config-csvjdbc.groovy"
		}
		harvest {
			directory = "file:input"
		}
	}
}