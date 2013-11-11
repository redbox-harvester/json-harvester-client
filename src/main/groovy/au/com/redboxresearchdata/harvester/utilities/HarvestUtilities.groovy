package au.com.redboxresearchdata.harvester.utilities

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONAware
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

public class HarvestUtilities {
	private static final Logger log = Logger.getLogger(HarvestUtilities.class);
	private static final String ARRAY_ERROR = "The size of the envelope must be smaller than the source"
	private static final String ITERATE_ERROR = "The compared objects do not contain compatible types"
	private static final String MAP_ERROR = "Cannot find useful starting point"

	private HarvestUtilities() {
		throw new IllegalAccessException("utilities class, with only static methods, should not be instantiated.")
	}

	private static def showError = { String message ->
		throw new UnsupportedOperationException(message)
	}

	private static def addToTarget = { target, sourceElement->
		target.add(sourceElement)
	}

	private static def putInTarget = {target, sourceKey, sourceValue->
		target.put(sourceKey, sourceValue)
	}

	private static def stepIntoJsonArray(JSONArray source, JSONArray target, sourceElement, i, Expando expando) {
		if (i < target.size() && (sourceElement instanceof JSONAware)) {
			expando.parentFunction(sourceElement, target.get(i), expando)
		} else {
			expando.altFunction(target, sourceElement)
		}
	}

	private static def stepIntoJsonObject(JSONObject source, JSONObject target, sourceElement, Expando expando) {
		def sourceKey = sourceElement.key
		def sourceValue = sourceElement.value

		if ( target.containsKey(sourceKey) && (sourceValue instanceof JSONAware)) {
			expando.parentFunction(sourceValue, target[sourceKey], expando)
		} else {
			expando.altFunction(target, sourceKey, sourceValue)
		}
	}

	private static def checkAndMerge = { source, target, Expando expando->
		expando.parentFunction = checkAndMerge
		source.eachWithIndex{ sourceElement, i ->
			if (source instanceof JSONArray && target instanceof JSONArray) {
				expando.altFunction = addToTarget
				stepIntoJsonArray(source, target, sourceElement, i, expando)
			} else if (source instanceof JSONObject && target instanceof JSONObject) {
				expando.altFunction = putInTarget
				stepIntoJsonObject(source, target, sourceElement, expando)
			} else {
				showError(ITERATE_ERROR)
			}
		}
        return target
	}

	/**
	 * This is a deep merge function for 2 json documents. The source is used to add data to the target.
	 * JSONObjects and JSONArrays are checked to allow nested data to be checked without performing a shallow clobber.
	 * Is in untested against very complicated json data, but does work at levels deeper than shallow merge.
	 * @param src the json object used to get new data
	 * @param tgt  the target of the merge. New data will be added. Pre-existing data will be clobbered
	 * @return merged target
	 */
	static JSONObject deeperMerge(final JSONObject src, final String tgt) {
		def expando = new Expando()
		JSONParser parser = new JSONParser()
		JSONObject initial = parser.parse(tgt)
		JSONObject target = checkAndMerge(src, initial, expando)
        return target
	}

	private static def checkAndUnpack={ source, target, Expando expando ->
		expando.completed = target
		expando.parentFunction = checkAndUnpack
		source.eachWithIndex{ sourceElement, i->
			if (source instanceof JSONArray && target instanceof JSONArray) {
				expando.altFunction = {showError(ARRAY_ERROR)}
				stepIntoJsonArray(source, target, sourceElement, i, expando)
			} else if (source instanceof JSONObject && target instanceof JSONObject) {
				expando.altFunction = {showError(MAP_ERROR)}
				stepIntoJsonObject(source, target, sourceElement, expando)
			} else {
				showError(ITERATE_ERROR)
			}
		}
		return expando.completed
	}

	static JSONObject unpackCollection(final JSONObject source, final JSONObject envelope) {
		def expando = new Expando()
		JSONObject result = checkAndUnpack(envelope, source, expando)
		return result
	}

	static def slurpBody(String source) {
		def sourceSlurper = new JsonSlurper().parseText(source)
		def sourceBody = sourceSlurper.data.data

		String sourceString = JsonOutput.toJson(sourceBody)

		JSONParser parser = new JSONParser()
		def sourceJson
		if (sourceBody instanceof List<?>) {
			sourceJson = (JSONArray)parser.parse(sourceString);
		} else {
			sourceJson = (JSONObject)parser.parse(sourceString);
		}

		return sourceJson
	}

	static def addDefaultToMultiple(final JSONAware source, final String target) {
		JSONArray resultCollection = new JSONArray()
		source.each{ element->
			JSONObject result = deeperMerge(element, target)
			resultCollection.add(result)
		}
		return resultCollection
	}
}