package au.com.redboxresearchdata.types;

import static org.junit.Assert.*

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Test

public class GroovyJsonTest extends GroovyTestCase {

	@Test
	public void test8() {
		String second = getExtraString()
		String first = getTestString()
		
		JSONParser p = new JSONParser();
		JSONObject firstJ = (JSONObject)p.parse(first);
		JSONObject secondJ = (JSONObject)p.parse(second);

		JSONObject completed = HarvestUtilities.deeperMerge(firstJ, secondJ)
		System.out.println(completed.toJSONString())
		
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
