package au.com.redboxresearchdata.harvester.utilities

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONAware
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

import au.com.redboxresearchdata.types.TypeFactory

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
		JSONObject jsonTemplate = createJsonObject(tgt)
		JSONObject target = checkAndMerge(src, jsonTemplate, expando)
		return target
	}

	private static def createJsonObject(String target) {
		JSONParser parser = new JSONParser()
		JSONObject json = parser.parse(target)
		return json
	}

	//	private static def checkAndUnpack={ source, target, Expando expando ->
	//		expando.completed = target
	//		expando.parentFunction = checkAndUnpack
	//		source.eachWithIndex{ sourceElement, i->
	//			if (source instanceof JSONArray && target instanceof JSONArray) {
	//				expando.altFunction = {showError(ARRAY_ERROR)}
	//				stepIntoJsonArray(source, target, sourceElement, i, expando)
	//			} else if (source instanceof JSONObject && target instanceof JSONObject) {
	//				expando.altFunction = {showError(MAP_ERROR)}
	//				stepIntoJsonObject(source, target, sourceElement, expando)
	//			} else {
	//				showError(ITERATE_ERROR)
	//			}
	//		}
	//		return expando.completed
	//	}
	//
	//	/**
	//	 * Uses the sourceEnvelope to find the inner json element/object that the envelope contains.
	//	 * @param source: source record(s) with envelope
	//	 * @param envelope: the envelope to remove from source(s)
	//	 * @return: inner json object
	//	 */
	//	static JSONObject unpackCollection(final JSONObject source, final String envelope) {
	//		def expando = new Expando()
	//		JSONObject jsonEnvelope = createJsonObject(envelope)
	//		JSONObject result = checkAndUnpack(jsonEnvelope, source, expando)
	//		return result
	//	}


	//TODO : remove dependence on hard-coding of "data.data"
	/**
	 * (Alternative to slurper is to use method unpackCollection)
	 * @param source full text including envelope and body.
	 * TODO : At the moment this method and the alternative can find a point within text to start at for adding defaults
	 * with deeperMerge. However finding the starting point is not enough. It needs to remove and remember this envelope to
	 * be able to re-add it around the updated body text at the end of deeperMerge. Currently this method does not take into
	 * account the added field type: JsonService, either
	 * @return
	 */
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

	static JSONObject addEnvelope(JSONAware source, String type) {
		String prefix = TypeFactory.getJsonHeaderStr(type)
		String suffix = TypeFactory.getJsonFooterStr(type)
		StringBuilder enveloped = new StringBuilder(source.toString())
				.insert(0, prefix)
				.append(suffix)

		JSONObject jsonEnveloped = createJsonObject(enveloped.toString())
		return jsonEnveloped
	}

	/**
	 * Calls deeperMerge for multiple json records, applying to jsonTemplate.
	 * @param source: source json object containing multiple json records
	 * @param target: target json template containing default values and/or required properties
	 * @return
	 */
	static def addDefaultToMultiple(final JSONAware source, final String target) {
		JSONArray resultCollection = new JSONArray()
		source.each{ element->
			JSONObject result = deeperMerge(element, target)
			resultCollection.add(result)
		}
		return resultCollection
	}

	/**
	 * convenience method to remove header, footer applied by TypeFactory, apply defaults, and then re-apply header and footer.
	 * 
	 * @param wrappedSource: source with header and footer
	 * @param targetDefaults: default properties that will be applied for each record
	 * @param type: type as defined in TypeFactory
	 * @return wrapped records with defaults applied.
	 */
	static String addDefaultToWrappedRecords(final String wrappedSource, final String targetDefaults, final String type) {
		JSONAware unwrapped = slurpBody(wrappedSource)

		JSONAware completed = HarvestUtilities.addDefaultToMultiple(unwrapped, targetDefaults)

		JSONObject rewrapped = HarvestUtilities.addEnvelope(completed, type)
		
		return rewrapped.toString()
	}
}