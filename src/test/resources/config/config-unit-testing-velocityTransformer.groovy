environments {
	test {
		velocityTransformer {
			templateDir = "src/test/resources/templates"
			testData {				
				templates = ["unit-test-template1.vm","unit-test-template2.vm"]
				scripts {
					preVelocity = [["src/test/resources/scripts/preExec-velocityTransformer.groovy":""]]
					postVelocity = [["src/test/resources/scripts/postExec-velocityTransformer.groovy":""]]
				}
			}
		}
	}
}