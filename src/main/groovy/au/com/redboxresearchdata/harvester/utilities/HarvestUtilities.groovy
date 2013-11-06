package au.com.redboxresearchdata.harvester.utilities

import groovy.json.JsonException

import org.apache.log4j.Logger;
import org.json.simple.JSONArray
import org.json.simple.JSONAware
import org.json.simple.JSONObject;

public class HarvestUtilities {
	
	private HarvestUtilities() {
		throw new IllegalAccessException("utilities class, with only static methods, should not be instantiated.")
	}
	

	/**
	 * This is a deep merge function for 2 json documents. The source is used to add data to the target.
	 * JSONObjects and JSONArrays are checked to allow nested data to be checked without performing a shallow clobber.
	 * Is in untested against very complicated json data, but does work at levels deeper than shallow merge.
	 * @param src the json object used to get new data
	 * @param tgt  the target of the merge. New data will be added. Pre-existing data will be clobbered
	 * @return merged target
	 * @throws JsonException
	 */
	public static JSONObject deeperMerge(final JSONObject src, final JSONObject tgt) throws JsonException {
		
		def checkAndMerge
		checkAndMerge = { source, target->
			source.eachWithIndex{sourceElement, i ->
				if (source instanceof JSONArray && target instanceof JSONArray) {
						if (i >= ((JSONArray)target).size()) {
							((JSONArray)target).add(sourceElement)
						} else {
							checkAndMerge(sourceElement, target[i])
						}
				} else if (source instanceof JSONObject && target instanceof JSONObject) {
					Object sourceKey = sourceElement.key
					Object sourceValue = sourceElement.value
					
					if ( (!(target.containsKey(sourceKey))) || (!(sourceValue instanceof JSONAware))) {
						target.put(sourceKey, sourceValue);
					} else {
						checkAndMerge(sourceValue, target[sourceKey])
					}
				} else throw new UnsupportedOperationException("The compared objects do not contain compatible types")
			}
		}
		
		JSONObject target = checkAndMerge(src, tgt)
		return target;
	}
}
