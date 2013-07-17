import org.apache.log4j.Logger
import org.apache.commons.io.FilenameUtils

/*
 *
 * Scripts are passed the ff. variables:
 *
 * data - the Map instance representing a single 'record' of data.
 * type - the Type name
 * config - the groovy.util.ConfigObject
 * log - a org.apache.log4j.Logger
 * scriptPath - the path of this script
 * environment - the current environment
 * configPath - the script's configuration (optional)
 *
 * And must set a global 'data' Map instance. The keys must match the field names of the Type. Setting 'data' to null invalidates the record.
 * 
 * The script can also set a 'message' global variable.
 *
 */
if (!configPath) {
	configPath = "${FilenameUtils.removeExtension(scriptPath)}-config.groovy"
} 
log.debug("Using ${configPath}")
def configFile = new File(configPath)
if (configFile.exists()) {
	log.debug("Attempting to load script config '${configPath}' using environment:${config.environment}")
	def scriptConfig= new ConfigSlurper(config.environment).parse(configFile.toURI().toURL())
	config = scriptConfig 
}
def preProc = new Filter(config:config, data:data, type:type, log:log)
preProc.process()
data = preProc.data
message = preProc.message
/**
 *  A sample implementation of a pre-assembly class. 
 * 
 *  This implementation filters fields using regular expressions. 
 *  
 * @author Shilo Banihit
 *
 */

class Filter {
	ConfigObject config
	Map data
	String type
	String message
	Logger log
	
	/**
	 * Uses Matcher.find(filter)
	 */
	public void filter() {	
		for (filterField in config.types[type].filters?.keySet() ){
			for (filter in config.types[type].filters[filterField]) {
				def srcData = null
				if (filterField == "all") {
					srcData = data
				} else {
					srcData = [data[filterField]]
				}
				for (fldData in srcData){
					if (log.isDebugEnabled()) {
						log.debug("${filterField} = ${fldData} filtering with $filter")
					}
					if (fldData =~ filter) {
						message = "Record failed while filtering,  ${filterField}:'${fldData}' snagged with '${filter}' "
						data = null
						return
					}
				}
				
			}		
		}
		if (data) message = "Data passed filtration."
	}
			
	public void process() {
		if (data) {
			if (log.isDebugEnabled()) {
				log.debug("RegexFilter executing filter...")
			}
			filter()
		} else {
			message = "No data to filter."
		}
		
		if (log.isDebugEnabled()) {
			log.debug(message)
		}
	}
}

