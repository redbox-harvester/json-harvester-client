package au.com.redboxresearchdata.harvester.httpclient;

import java.io.File;

import org.json.simple.JSONArray;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import com.googlecode.fascinator.common.JsonObject;
import com.googlecode.fascinator.common.JsonSimple;

/**
 * Does the email lookup to Mint.
 *
 * @author Devika Indla
 * @since 1.0
 *
 */

public class MintLookup {
	private LookupProperty lookupProperty;
    private String email;	
	public LookupProperty getLookupProperty() {
		return lookupProperty;
	}

	public void setLookupProperty(LookupProperty lookupProperty) {
		this.lookupProperty = lookupProperty;
	}

	public MintLookup(String email){
		this.email = email;
		String contextFilePath = "spring-integration-Lookup.xml";
		String absContextPath = "config/integration/" + contextFilePath;
		File contextFile = new File(absContextPath);
		final AbstractApplicationContext context;
		if (!contextFile.exists()) {
			absContextPath = "classpath:"+absContextPath;
			context =
					new ClassPathXmlApplicationContext(absContextPath);
		} else {
			absContextPath = "file:" + absContextPath; 
			context =
					new FileSystemXmlApplicationContext(absContextPath);
		}
	    lookupProperty = (LookupProperty) context.getBean("lookUpURLBean");
		invokeProxy();
	}
	
	  public void invokeProxy(){
		    String baseUrl = lookupProperty.getLookupURLL();
		    baseUrl = baseUrl.concat(email);
		    String mintUserName = lookupProperty.getMintUserName();
		    String mintPassword = lookupProperty.getMintPassword();
		    String resultsProp = lookupProperty.getResults();
		    String resultMetadataProp = lookupProperty.getResultMetadata();
		    String allProp = lookupProperty.getAll();
		    String givenNameProp = lookupProperty.getGivenNameProp();
		    String familyNameProp = lookupProperty.getFamilyNameProp();
		    String dcItentifierProp = lookupProperty.getDcIdentifierProp();
		    BasicHttpClient basicHttpClient = new BasicHttpClient(baseUrl, mintUserName, mintPassword);
			HttpMethodBase baseMethod = new GetMethod(baseUrl);
		 	try {
				basicHttpClient.executeMethod(baseMethod);
				String proxyOutput = IOUtils.toString(baseMethod.getResponseBodyAsStream(), "UTF-8");
				 JsonSimple json = new JsonSimple(proxyOutput);
				 JsonObject jsonObject = json.getJsonObject();
				 JSONArray results = (JSONArray) jsonObject.get(resultsProp);
				 for(Object result : results){
					 Map mapResult =  (Map)result;
					 String dcIdententifier = (String) mapResult.get(dcItentifierProp);
					 if(null != dcIdententifier){
						 lookupProperty.setDcIdentifier(dcIdententifier);
					 }
					 Map mapResultMetadata =  (Map) mapResult.get(resultMetadataProp);
					 Map allMapResult = (Map) mapResultMetadata.get(allProp);
					 JSONArray givenNameJson = (JSONArray)allMapResult.get(givenNameProp);
					 JSONArray familyNameJson = (JSONArray)allMapResult.get(familyNameProp);
					 if(null != givenNameJson){
					    for(Object givenName : givenNameJson){
						   lookupProperty.setGivenName((String)givenName);
					    }
					 }
				     if(null != familyNameJson){
					   for(Object familyName : familyNameJson){
						 lookupProperty.setFamilyName((String)familyName);
					    }
				     }
						
				 }
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
}
