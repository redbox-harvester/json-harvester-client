package au.com.redboxresearchdata.types;

import static org.junit.Assert.*

import org.json.simple.JSONAware;
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Test

import au.com.redboxresearchdata.harvester.utilities.HarvestUtilities

public class GroovyJsonTest extends GroovyTestCase {

	@Test
	public void testDeepMerge() {
		String second = getExtraString()
		String first = getTestString()

		JSONParser p = new JSONParser();
		JSONObject firstJ = (JSONObject)p.parse(first);
		JSONObject secondJ = (JSONObject)p.parse(second);

		JSONObject completed = HarvestUtilities.deeperMerge(firstJ, secondJ)

		System.out.println(completed.toJSONString())
		System.out.println()
	}

	@Test
	public void testStartingPoint() {
		String first = getTestString()
		String second = getUnpackageString()
		JSONParser p = new JSONParser()
		JSONObject firstJ = (JSONObject)p.parse(first);
		JSONObject secondJ = (JSONObject)p.parse(second);

		JSONObject completed = HarvestUtilities.unpackCollection(firstJ, secondJ)
		System.out.println(completed.toJSONString())
		System.out.println()
	}

	@Test
	public void testSlurpBody() {
		String source = getTestString()
		JSONAware completed = HarvestUtilities.slurpBody(source)
		System.out.print(completed.toJSONString())
		System.out.println()
	}

	@Test
	public void testSlurpAndAddDefaultToMultipleRecords() {
		String source = getTestString()
		JSONAware sourceBody = HarvestUtilities.slurpBody(source)

		String template = getDefault()
		JSONParser parser = new JSONParser()
		JSONObject templateJson = (JSONObject)parser.parse(template);

		JSONAware completed = HarvestUtilities.addDefaultToMultiple(sourceBody, templateJson)

		System.out.print(completed.toJSONString())
		System.out.println()
	}



	private String getUnpackageString() {
		String testExtra = """{
 "data": {
    "data": [
      ]   
   }
}"""
	}


	private String getDefault() {
		String testExtra = """{
        "datasetId":"1",
        "owner":"admin",
        "attachmentList" : ["tfpackage", "workflow.metadata"],
        "customProperties" : ["file.path"],
        "varMap" : {
            "file.path" : "${fascinator.home}/packages/<oid>.tfpackage"            
        },
        "attachmentDestination" : {
            "tfpackage":["<oid>.tfpackage","metadata.json","$file.path"], 
            "workflow.metadata":["workflow.metadata"]
        },
        "workflow.metadata" : {
            "id":"dataset",
            "step":"metadata-review",
            "pageTitle":"Metadata Record",
            "label":"Metadata Review",
            "formData" : {
                "title" : "Test-Record-JSON Harvester",
                "description": "Test description"
            }                    
        },
        "tfpackage": {} 
}"""
		return testExtra;
	}

	private String getExtraString() {
		String testExtra = """{
 "type":"DatasetJson",
 "data": {
    "data": [
		{
        "datasetId":"",
		"owner":""
		}
      ]   
   }
}"""
		return testExtra;
	}

	private String getTestString() {
		String test = """{
 "data": {
    "data": [
        {
        "datasetId":"1",
        "owner":"admin",
        "attachmentList" : ["tfpackage", "workflow.metadata"],
        "attachmentDestination" : {
            "tfpackage":["<oid>.tfpackage","metadata.json"], 
            "workflow.metadata":["workflow.metadata"]
        },
        "workflow.metadata" : {
            "id":"dataset",
            "formData" : {
                "title" : "Test-Record-JSON Harvester",
            }                    
        },
        "tfpackage": {                      
            "title": "Test-Record-JSON Harvester",
            "redbox:submissionProcess.dc:date": "",
            "metaList": [
                "dc:title",
                "xmlns:anzsrc"
            ]
          }
        }
      ]   
   }
}"""
		return test;
	}
}
