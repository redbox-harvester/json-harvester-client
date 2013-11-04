package au.com.redboxresearchdata.types;

import static org.junit.Assert.*;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import groovy.json.JsonBuilder;

import org.junit.Test;

import net.minidev.json.JSONObject
import net.minidev.json.parser.JSONParser;

public class GroovyJsonTest extends GroovyTestCase {

	@Test
	public void test() {
		JSONObject obj1 = new JSONObject();
		obj1.put("name","foo");
		obj1.put("num",new Integer(100));
		obj1.put("balance",new Double(1000.21));

		JSONObject obj2 = new JSONObject();
		obj2.put("is_vip",new Boolean(true));
		obj2.put("nickname",null);
		obj2.putAll(obj1);
		System.out.print(obj2);
	}

	@Test
	public void test2() {
		def builder = new groovy.json.JsonBuilder()

		def root = builder {
			type  'DatasetJson'
			data  {
			}
		}
		System.out.println(builder.toString())
	}

	@Test
	public void test3() {
		def builderA = new groovy.json.JsonBuilder()

		def root = builderA {
			type  'DatasetJson'
			data  {
			}
		}
		System.out.println("a:" + builderA.toString())

		JSONObject testA = new JSONObject()


		System.out.println("testA:" + testA)

		def builderB = new JsonBuilder()
		//
		//		def rootB = builderB {
		//			yellow 'flowers'
		//		}

		System.out.println("b: " + builderB.toString())

	}

	@Test
	public void test5() {
		String test = getTestString();
		System.out.println("start");

		mergeJsonTestStrings(getTestString(), getExtraString())
	}


	//	private JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
	//
	//			Iterator<String> fieldNames = updateNode.fieldNames();
	//			while (fieldNames.hasNext()) {
	//
	//				String fieldName = fieldNames.next();
	//				JsonNode jsonNode = mainNode.get(fieldName);
	//				// if field doesn't exist or is an embedded object
	//				if (jsonNode != null && jsonNode.isObject()) {
	//					merge(jsonNode, updateNode.get(fieldName));
	//				}
	//				else {
	//					if (mainNode instanceof ObjectNode) {
	//						// Overwrite field
	//						JsonNode value = updateNode.get(fieldName);
	//						((ObjectNode) mainNode).put(fieldName, value);
	//					}
	//				}
	//
	//			}
	//
	//			return mainNode;
	//		}
	//

	@Test
	public void test6() {
		def slurper = new JsonSlurper();

		def result = slurper.parseText(getExtraString());

		result.each { println it}
	}
	
	
	
	

	@Test
	public void test7() {
		def slurper = new JsonSlurper();

		def syncIterator = new SyncIterator(slurper.parseText(getExtraString()), slurper.parseText(getTestString()))

		def c={m,n->
			if (m==null) then m=n
			else if (n==null) then n=m
			else if (m != n) {
				syncIterator = new SyncIterator(m, n)
			}
		}

		while (syncIterator.hasNext()) {
			System.print("next.....")
			def nextOnes = syncIterator.next()
			System.out.print(nextOnes)
			System.out.println()
			
//			c(nextOnes)

		}
	}
		
	@Test
	public void test8() {
		String second = getExtraString()
		String first = getTestString()
		
		net.minidev.json.parser.JSONParser p = new net.minidev.json.parser.JSONParser();
		net.minidev.json.JSONObject firstJ = (net.minidev.json.JSONObject)p.parse(first);
		net.minidev.json.JSONObject secondJ = (net.minidev.json.JSONObject)p.parse(second);

		JSONObject completed = SyncIterator.deepMerge(firstJ, secondJ)
		System.out.println(completed.toJSONString())
		
	}
		


	private void mergeJsonTestStrings(String json1, String json2) {
		//		String json1 = "{'car':{'color':'blue'}}";
		//		String json2 = "{'car':{'size':'3.5m'}}";

		net.minidev.json.parser.JSONParser p = new net.minidev.json.parser.JSONParser();
		net.minidev.json.JSONObject o1 = (net.minidev.json.JSONObject)p.parse(json1);
		net.minidev.json.JSONObject o2 = (net.minidev.json.JSONObject)p.parse(json2);

		o1.merge(o2);

		System.out.println(o1)

		String outputString = o1.toJSONString()

		//		System.out.println(JsonOutput.prettyPrint(outputString))

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
