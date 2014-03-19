environments {
	test {
		velocityTransformer {
			templateDir = "src/test/resources/templates/"
			scriptDir = "src/test/resources/scripts/"
			testData {				
				templates = ["unit-test-template1.vm","unit-test-template2.vm"]
				scripts {
					preVelocity = [["preExec-velocityTransformer.groovy":""]]
					postVelocity = [["postExec-velocityTransformer.groovy":""]]
				}
			}
		}
	}
}