package au.com.redboxresearchdata.harvester.httpclient;

/**
 * Bean or POJO for lookup to Mint.
 *
 * @author Devika Indla
 * @since 1.0
 *
 */

public class LookupProperty {
	
   private String lookupURL;
   private  String mintUserName;
   private  String mintPassword;
   private  String givenName;
   private  String familyName;
   private  String givenNameProp;
   private  String familyNameProp;
   private String dcIdentifierProp;
   private String dcIdentifier;
   private String resultMetadata;
   private String results;
   private String all;
   
   public LookupProperty(){
		
   }
   
   public String getDcIdentifier() {
	return dcIdentifier;
   }

   public void setDcIdentifier(String dcIdentifier) {
	this.dcIdentifier = dcIdentifier;
   }

   public String getDcIdentifierProp() {
    	return dcIdentifierProp;
   }

   public void setDcIdentifierProp(String dcIdentifierProp) {
    	this.dcIdentifierProp = dcIdentifierProp;
   }

   public String getGivenNameProp() {
	    return givenNameProp;
   }

   public void setGivenNameProp(String givenNameProp) {
	  this.givenNameProp = givenNameProp;
   }
 
   public String getFamilyNameProp() {
	 return familyNameProp;
   }

   public void setFamilyNameProp(String familyNameProp) {
	this.familyNameProp = familyNameProp;
   }

   
   public String getResultMetadata() {
	return resultMetadata;
   }

   public void setResultMetadata(String resultMetadata) {
	  this.resultMetadata = resultMetadata;
   }

   public String getResults() {
	 return results;
   }

   public void setResults(String results) {
	  this.results = results;
   }

   public String getAll() {
	return all;
   }

   public void setAll(String all) {
	this.all = all;
   }

   public String getLookupURLL() {
	   return lookupURL;
   }

   public void setLookupURL(String lookupURL) {
	  this.lookupURL = lookupURL;
   }  
   
   public String getMintUserName() {
	 return mintUserName;
   }

   public void setMintUserName(String mintUserName) {
	   this.mintUserName = mintUserName;
   }

   public String getMintPassword() {
	 return mintPassword;
   }

   public void setMintPassword(String mintPassword) {
	   this.mintPassword = mintPassword;
   }
   
   public String getGivenName() {
	return givenName;
   }
 
   public  void setGivenName(String givenName) {
	   this.givenName = givenName;
   }
   
   public String getFamilyName() {
	return familyName;
   }

   public void setFamilyName(String familyName) {
	   this.familyName = familyName;
   }
}
