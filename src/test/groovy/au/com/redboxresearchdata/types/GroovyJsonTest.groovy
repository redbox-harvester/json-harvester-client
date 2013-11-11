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
        JSONObject firstJ = (JSONObject) p.parse(first);

        JSONObject completed = HarvestUtilities.deeperMerge(firstJ, second)

        System.out.println(completed.toJSONString())
        System.out.println()
    }

    @Test
    public void testStartingPoint() {
        String first = getTestString()
        String second = getUnpackageString()
        JSONParser p = new JSONParser()
        JSONObject firstJ = (JSONObject) p.parse(first);
        JSONObject secondJ = (JSONObject) p.parse(second);

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
        String source = getTestString2()
        JSONAware sourceBody = HarvestUtilities.slurpBody(source)

        JSONAware completed = HarvestUtilities.addDefaultToMultiple(sourceBody, getDefault())

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
        "workflow.metadata" : {
            "id":"test",
            "step":"metadata-review",
            "pageTitle":"Metadata Record",
            "label":"Metadata Review",
            "formData" : {
                "title" : "test",
                "description": "Test description"
            }                    
        }
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
            "id":"",
            "formData" : {
                "title" : ""
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

    private String getTestString2() {
        String test = """{
 "data": {
    "data": [
        {
            "workflow.metadata" : {
                "id":"dataset",
                 "formData" : {
                    "title" : "Test-Record-JSON Harvester"
                 }
            }
        },
		{
            "datasetId":"3",
            "owner":"Invalid JSON Document"
        }
    ]
 }
}"""
        return test;
    }
}
